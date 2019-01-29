package jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterBatchUpdateUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import bean.YDException;
import domain.entity.AbstractTableEntity;
import jdbc.INamedParameters;
import jdbc.IParameters;
import jdbc.IResultSetExtractor;
import jdbc.IRowCallbackHandler;
import jdbc.SqlWatch;
import jdbc.dialect.IDialect;
import jdbc.dialect.MysqlDialect;
import pubUtils.BeanUtils;



/**
 * @author wangpeng
 */
public abstract class BaseJdbcDao {
    private final static int     WARNTIME;                   // 执行时间超长写日志的伐值
    private final static int     ROWS_THRESHOLD;             // 执行语句涉及行数限制伐值
    private final static String  ROWS_EXCEEDS_MSG;           // 执行语句涉及行数超限错误信息
    private final static int     DEFAULT_CACHE_LIMIT = 4096; //SQL缓存个数
    private final static int     WARN_ROWNUM         = 20000;
    private static Logger dbLogger = Logger.getLogger(BaseJdbcDao.class);
    static {
        WARNTIME = Integer.getInteger("DBLOG.WARNTIME", 1);
        ROWS_THRESHOLD = Integer.getInteger("DBLOG.ROWS_THRESHOLD", -1);
        ROWS_EXCEEDS_MSG = "the number of rows affected exceeds the threshold[" + ROWS_THRESHOLD
            + "]";
    }
    
    private volatile int                 cacheLimit     = DEFAULT_CACHE_LIMIT;
    private final Map<String, ParsedSql> parsedSqlCache = initParsedSqlCache();
    
    protected DataSource            	 dataSource;
    protected JdbcTemplate               jdbcTemplate;                                // JdbcOperations对象
    protected IDialect                   dialect        = MysqlDialect.getInstance(); // SQL方言

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public DataSource getDataSource() {
		return dataSource;
	}

	public void setDialect(IDialect dialect) {
        this.dialect = dialect;
    }
    
    /**
     * Specify the maximum number of entries for this template's SQL cache.
     * Default is 256.
     */
    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }
    
    /**
     * Return the maximum number of entries for this template's SQL cache.
     */
    public int getCacheLimit() {
        return this.cacheLimit;
    }
    
    private Map<String, ParsedSql> initParsedSqlCache() {
        return new LinkedHashMap<String, ParsedSql>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
            private static final long serialVersionUID = 7413677439224538807L;
            
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ParsedSql> eldest) {
                return size() > getCacheLimit();
            }
        };
    }
    
    /**
     * Obtain a parsed representation of the given SQL statement.
     * <p>The default implementation uses an LRU cache with an upper limit
     * of 256 entries.
     * @param sql the original SQL
     * @return a representation of the parsed SQL statement
     */
    protected ParsedSql getParsedSql(String sql) {
        if (getCacheLimit() <= 0) {
            return NamedParameterUtils.parseSqlStatement(sql);
        }
        synchronized (this.parsedSqlCache) {
            ParsedSql parsedSql = this.parsedSqlCache.get(sql);
            if (parsedSql == null) {
                parsedSql = NamedParameterUtils.parseSqlStatement(sql);
                this.parsedSqlCache.put(sql, parsedSql);
            }
            return parsedSql;
        }
    }
    
    protected class RowCallbackExtractor implements IResultSetExtractor<Object> {
        private final IRowCallbackHandler rch;
        
        protected RowCallbackExtractor(IRowCallbackHandler rch) {
            this.rch = rch;
        }
        
        /** (non-Javadoc)
         * @see com.eshore.crmpub.jdbc.IResultSetExtractor#extractData(java.sql.ResultSet)
         */
        public Object extractData(ResultSet rs) throws SQLException {
            int rowNum = 0;
            while (rs.next()) {
                ++rowNum;
                this.rch.processRow(rs);
                if (0 == (rowNum % WARN_ROWNUM)) {
                    dbLogger.warn("extractData rowNum=" + rowNum);

                }
            }
            if (rowNum >= WARN_ROWNUM) {
                dbLogger.warn("extractData all rowNum=" + rowNum);
            }
            return null;
        }
        
    }
    
    protected <T> T query(String sql, IParameters params, IResultSetExtractor<T> rse,
        boolean stream) {
        SqlWatch sw = new SqlWatch();
        try {
            sw.start();
            
            if (stream) {//流式读取
                ReadOnlyPreparedStatementCreator psc = new ReadOnlyPreparedStatementCreator(sql);
                if (params == null || params.isEmpty()) {// 没有参数
                    return jdbcTemplate.query(psc, new MyResultSetExtractor<T>(rse, sql, null));
                } else {
                    Object[] args = params.toArray();
                    PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(args);
                    return jdbcTemplate.query(psc, pss,
                        new MyResultSetExtractor<T>(rse, sql, params));
                }
            } else {
                if (params == null || params.isEmpty()) {// 没有参数
                    return jdbcTemplate.query(sql, new MyResultSetExtractor<T>(rse, sql, null));
                } else {
                    Object[] args = params.toArray();
                    return jdbcTemplate.query(sql, args,
                        new MyResultSetExtractor<T>(rse, sql, params));
                }
            }
        } catch (DataAccessException e) {
            Throwable t = e.getCause();
            if (t == null) {
                t = e;
            }
            sw.setError(t.getMessage());
            throw new YDException(""+ t);
        } catch (Throwable t) {
            sw.setError(t.getMessage());
            // 抛异常以便回滚事务
            throw new YDException(""+ t);
        } finally {
            sw.stop();
            writeLog(sql, params, sw);
        }
    }
    
    protected PreparedStatementCreator getPreparedStatementCreator(String sql,
        SqlParameterSource paramSource, boolean stream) {
        ParsedSql parsedSql = getParsedSql(sql);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql,
            paramSource);
        List<?> paramList = Arrays.asList(params);
        if (stream) {//流式读取
            return new NamedReadOnlyPreparedStatementCreator(sqlToUse, paramList,
                declaredParameters);
        } else {
            PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse,
                declaredParameters);
            return pscf.newPreparedStatementCreator(params);
        }
    }
    
    private class MyResultSetExtractor<T> implements ResultSetExtractor<T> {
        private final IResultSetExtractor<T> rse;
        private final long                   startTime;
        private final String                 sql;
        private final Object                 params;
        
        private MyResultSetExtractor(IResultSetExtractor<T> rse, String sql, Object params) {
            this.rse = rse;
            this.startTime = System.currentTimeMillis();
            this.sql = sql;
            this.params = params;
        }
        
        /** (non-Javadoc)
         * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
         */
        public T extractData(ResultSet rs) throws SQLException, DataAccessException {
            long st = System.currentTimeMillis();
            try {
                long openCursorTime = st - startTime;
                if (dbLogger.isEnabledFor(Level.WARN) && openCursorTime > 0 && openCursorTime >= WARNTIME) {
                    dbLogger.warn("openCursorTime " + openCursorTime //
                        +sql);
                    if (params != null) {
                        dbLogger.warn("param:" + params);
                    }
                }
                return rse.extractData(rs);
            } finally {
                long extractTime = System.currentTimeMillis() - st;
                if (dbLogger.isEnabledFor(Level.WARN) && extractTime > 0 && extractTime >= WARNTIME) {
                    dbLogger.warn("extractTime " + extractTime //
                        + sql);
                    if (params != null) {
                        dbLogger.warn("param:" + params);
                    }
                }
            }
        }
    }
    
    protected <T> T query(String sql, INamedParameters params, IResultSetExtractor<T> rse,
        boolean stream) {
        System.out.println(sql);
        SqlWatch sw = new SqlWatch();
        
        try {
            sw.start();
            
            SqlParameterSource paramSource = null;
            if (params == null || params.isEmpty()) {// 没有参数
                paramSource = EmptySqlParameterSource.INSTANCE;
            } else {
                paramSource = new MapSqlParameterSource(params);
            }
            
            PreparedStatementCreator psc = getPreparedStatementCreator(sql, paramSource, stream);
            return jdbcTemplate.query(psc, new MyResultSetExtractor<T>(rse, sql, params));
        } catch (DataAccessException e) {
            Throwable t = e.getCause();
            if (t == null) {
                t = e;
            }
            sw.setError(t.getMessage());
            throw new YDException("queryError:"+sw.getErrorMsg());
        } catch (Throwable t) {
            sw.setError(t.getMessage());
            // 抛异常以便回滚事务
            throw new YDException("queryError"+sw.getErrorMsg());
        } finally {
            sw.stop();
            writeLog(sql, params, sw);
        }
    }
    
    /**
     * 写日志
     * 
     * @param sql SQL语句
     * @param param 绑定变量参数
     * @param sw SQL语句监控对象
     */
    protected void writeLog(String sql, Object param, SqlWatch sw) {
        writeLog(sql, param, sw, WARNTIME);
    }
    
    /**
     * 写日志
     * 
     * @param sql SQL语句
     * @param param 绑定变量参数
     * @param sw SQL语句监控对象
     * @param overTime 超时阀值
     */
    protected void writeLog(String sql, Object param, SqlWatch sw, long overTime) {
        long t = sw.getTime();
        
        // 输出错误信息
        if (sw.isError()) {
            dbLogger.error(t + sql);
            if (param != null) {
                dbLogger.error("param:" + param);
            }
            dbLogger.error(sw.getErrorMsg());
            return;
        }
        
        // 执行时间过长
        if (dbLogger.isEnabledFor(Level.WARN) && t > overTime && overTime >= WARNTIME) {
            dbLogger.warn(t + sql);
            if (param != null) {
                dbLogger.warn("param:" + param);
            }
            return;
        }
        
        // 输出调试信息
        if (dbLogger.isDebugEnabled()) {
            dbLogger.debug(t + sql);
            if (param != null) {
                dbLogger.debug("param:" + param);
            }
        }
    }
    
    @SuppressWarnings({"unchecked", "rawtypes" })
    protected int[] batchUpdate(String sql, List<?> params) {
        int length = params.size();
        if (params == null || params.isEmpty()) {
            return new int[0];
        }
        
        SqlParameterSource batchArgs[] = new SqlParameterSource[length];
        
        int i = 0;
        for (Object param : params) {
            if (param instanceof Map) {
                batchArgs[i] = new MapSqlParameterSource((Map) param);
            } else if (param instanceof AbstractTableEntity) {
                batchArgs[i] = new MapSqlParameterSource(
                    (BeanUtils.toMap((AbstractTableEntity) param, null)));
            } else {
                batchArgs[i] = new BeanPropertySqlParameterSource(param);
            }
            ++i;
        }
        
        SqlWatch sw = new SqlWatch();
        try {
            sw.start();
            
            ParsedSql parsedSql = getParsedSql(sql);
            int nums[] = NamedParameterBatchUpdateUtils
                .executeBatchUpdateWithNamedParameters(parsedSql, batchArgs, jdbcTemplate);
            for (int j = 0; j < nums.length; ++j) {
                int num = nums[j];
                if (num > ROWS_THRESHOLD && ROWS_THRESHOLD > 0) {
                    String msg = "[" + num + "]" + ROWS_EXCEEDS_MSG;
                    dbLogger.error("rows exceeds batch args:" + batchArgs[j]);
                    throw new SQLException(msg);
                }
            }
            return nums;
        } catch (DataAccessException e) {
            Throwable t = e.getCause();
            if (t == null) {
                t = e;
            }
            sw.setError(t.getMessage());
            throw new YDException("数据库操作失败!" + sw.getErrorMsg()+t+sw.getErrorMsg());
        } catch (Throwable t) {
            sw.setError(t.getMessage());
            // 抛异常以便回滚事务
            throw new YDException("数据库操作失败!" + sw.getErrorMsg()+t+sw.getErrorMsg());
        } finally {
            sw.stop();
            writeLog(sql, "batchUpdate", sw, 3000);
        }
    }
    
    protected int update(String sql, INamedParameters params) {
        SqlWatch sw = new SqlWatch();
        try {
            sw.start();
            if (params == null || params.isEmpty()) {// 没有参数
                return jdbcTemplate.update(sql);
            }
            
            SqlParameterSource paramSource = new MapSqlParameterSource(params);
            PreparedStatementCreator psc = getPreparedStatementCreator(sql, paramSource, false);
            int num = jdbcTemplate.update(psc);
            if (num > ROWS_THRESHOLD && ROWS_THRESHOLD > 0) {
                String msg = "[" + num + "]" + ROWS_EXCEEDS_MSG;
                throw new SQLException(msg);
            }
            return num;
        } catch (DataAccessException e) {
            Throwable t = e.getCause();
            if (t == null) {
                t = e;
            }
            sw.setError(t.getMessage());
            throw new YDException("error"+sw.getErrorMsg()+t+sw.getErrorMsg());
        } catch (Throwable t) {
            sw.setError(t.getMessage());
            // 抛异常以便回滚事务
            throw new YDException("error"+sw.getErrorMsg()+t+sw.getErrorMsg());
        } finally {
            sw.stop();
            writeLog(sql, params, sw);
        }
    }
    
    public int update(String sql, IParameters params) {
        SqlWatch sw = new SqlWatch();
        try {
            sw.start();
            if (params == null || params.isEmpty()) {// 没有参数
                return jdbcTemplate.update(sql);
            }
            int num = jdbcTemplate.update(sql, params.toArray());
            if (num > ROWS_THRESHOLD && ROWS_THRESHOLD > 0) {
                String msg = "[" + num + "]" + ROWS_EXCEEDS_MSG;
                throw new SQLException(msg);
            }
            return num;
        } catch (DataAccessException e) {
            Throwable t = e.getCause();
            if (t == null) {
                t = e;
            }
            sw.setError(t.getMessage());
            throw new YDException("error"+sw.getErrorMsg()+t+sw.getErrorMsg());
        } catch (Throwable t) {
            sw.setError(t.getMessage());
            // 抛异常以便回滚事务
            throw new YDException("error"+sw.getErrorMsg()+t+sw.getErrorMsg());
        } finally {
            sw.stop();
            writeLog(sql, params, sw);
        }
    }
}
