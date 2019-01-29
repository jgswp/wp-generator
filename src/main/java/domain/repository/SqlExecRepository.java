package domain.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import domain.entity.AbstractTableEntity;
import domain.entity.AbstractTableEntityMeta;
import domain.entity.EntityManager;
import domain.entity.EntityMapper;
import jdbc.IJdbcDao;
import jdbc.INamedParameters;
import jdbc.IParameters;
import jdbc.IRowSet;
import jdbc.SqlDao;
import jdbc.core.ResultSetExtractor;
import pubUtils.StringHelper;

/**
 * 业务实体仓储基类（允许内部执行SQL语句）
 * 
 * @author wangpeng
 */
public abstract class SqlExecRepository {
    public Logger           logger           = Logger.getLogger(getClass());
    
    IJdbcDao jdbcDao;

	public void setJdbcDao(IJdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
	}
    
    public boolean isEmpty(String str) {
        return StringHelper.isEmpty(str);
    }
    
    public boolean isNotEmpty(String str) {
        return !StringHelper.isEmpty(str);
    }
    
    public boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    public boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }
    
//    /**
//     * 构建编程事务管理对象
//     * 
//     * @param propagationBehavior
//     *            事务级别定义（参考spring的TransactionDefinition类里的常量定义）
//     * @return TransactionManager
//     */
//    public ITransaction newTransaction(int propagationBehavior) {
//        return new TransactionManager(dataSource, propagationBehavior);
//    }
    
    /**
     * 获取数据库时间
     * 
     * @return 数据库时间
     */
    public Date getSysDate() {
        return jdbcDao.getSysDate();
    }
    
    /**
     * 查询序列
     * 
     * @param seqName 序列
     * @return 一个序列值
     */
    public long getSeqID() {
        return jdbcDao.getSeqID();
    }
    
    /**
     * 执行SQL
     * 
     * @param sql insert/update/delete等SQL语句
     * @param params 绑定变量参数
     * @return 返回处理记录数
     */
    public final int execSql(String sql, INamedParameters params) {
        return jdbcDao.execSql(sql, params);
    }
    
    /**
     * 执行SQL
     * 
     * @param sql insert/update/delete等SQL语句
     * @param params 绑定变量参数
     * @return 返回处理记录数
     */
    public final int execSql(String sql) {
        return jdbcDao.execSql(sql, (IParameters) null);
    }
    
    /**
     * 执行SQL
     * 
     * @param sql insert/update/delete等SQL语句
     * @param params 绑定变量参数
     * @return 返回处理记录数
     */
    public final int execSql(String sql, IParameters params) {
        return jdbcDao.execSql(sql, params);
    }
    
    /**
     * 查询SQL
     * 
     * @param sql SQL
     * @param params 查询参数
     * @return 记录集
     */
    public IRowSet query(String sql, IParameters params) {
        return jdbcDao.query(sql, params, new ResultSetExtractor(), false);
    }
    
    /**
     * 查询sql
     * 
     * @param sqlDao SQL查询
     * @return 记录集
     */
    public final IRowSet query(SqlDao sql) {
        return jdbcDao.query(sql);
    }
    
    
    public final <T> List<T> select(AbstractTableEntityMeta meta, SqlDao sql,
        Class<?> clazz) {
        Assert.notNull(meta, "entity has not been struck off the register");
        Assert.notNull(sql, "criterion is required; it must not be null");
        Assert.hasLength(sql.getSql(), "sql criterion is required; it must not be null");
        
        StringBuilder sqlUse = new StringBuilder(meta.getSelectSql()).append(" ")
            .append(sql.getSql());
        SqlDao sqlDao = new SqlDao(sqlUse.toString(), sql.getParamters());
        return jdbcDao.query(sqlDao,new EntityMapper<T>(meta, clazz));
    }
    
    public final <T extends AbstractTableEntity> List<T> select(AbstractTableEntityMeta meta,
        SqlDao query) {
        return select(meta, query, null);
    }
    
    public final <E extends AbstractTableEntity> List<E> select(Class<E> entityType,
    	SqlDao sqlDao) {
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(entityType);
        return select(meta, sqlDao);
    }
    
    public final <E extends AbstractTableEntity, T> List<T> select(Class<E> entityType,
    		SqlDao sqlDao, Class<T> clazz) {
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(entityType);
        return select(meta, sqlDao, clazz);
    }
}
