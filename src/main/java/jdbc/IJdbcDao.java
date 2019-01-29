package jdbc;

import java.util.Date;
import java.util.List;

import bean.MapBean;

/**
 * JDBC DAO
 * 
 * @author wangpeng
 */
public interface IJdbcDao {
    /**
     * 获取数据库时间
     * 
     * @return 数据库时间
     */
    public Date getSysDate();
    
    /**
     * 查询序列
     * 
     * @param seqName 序列
     * @return 一个序列值
     */
    public long getSeqID();
    
    /**
     * 执行SQL
     * 
     * @param sql insert/update/delete等SQL语句
     * @param params 绑定变量参数
     * @return 返回处理记录数
     */
    public int execSql(String sql, INamedParameters params);
    
    /**
     * 执行SQL
     * 
     * @param sql insert/update/delete等SQL语句
     * @param params 绑定变量参数
     * @return 返回处理记录数
     */
    public int execSql(String sql, IParameters params);
    
    /**
     * 查询SQL
     * 
     * @param sql SQL
     * @param params 查询参数
     * @return 记录集
     */
    public IRowSet query(String sql, IParameters params);
    
    /**
     * 查询sql
     * 
     * @param sql SQL查询参数
     * @return 记录集
     */
    public IRowSet query(SqlDao sqlQuery);
    
    /**
     * 查询sql
     * 
     * @param sql SQL查询参数
     * @return 记录集
     */
    public List<MapBean> queryForList(SqlDao sqlQuery);
    
    /**
     * 批量执行SQL语句
     * 
     * @param sql
     *            insert/update/delete SQL语句
     * @param values
     *            SQL语句多次执行的绑定变量
     */
    public int[] batchExecSql(String sql, List<?> params);
    
    /**
     * 查询SQL
     * 
     * @param sql SQL
     * @param params 查询参数
     * @param rse 记录集抽取接口
     */
    public <T> T query(String sql, IParameters params, IResultSetExtractor<T> rse, boolean stream);
    
    /**
     * SQL查询
     * @param sqlQuery SQL查询参数
     * @param rse 数据集抽取器
     * @param stream 是否启用流式读取
     * @return 抽取结果
     */
    public <T> T query(SqlDao sqlQuery, IResultSetExtractor<T> rse, boolean stream);
    
    /**
     * SQL查询
     * @param sql SQL
     * @param params SQL参数
     * @param rch 记录集处理器
     * @param stream 是否启用流式读取
     * @return 抽取结果
     */
    public <T> void query(String sql, IParameters params, IRowCallbackHandler rch, boolean stream);
    
    /**
     * SQL查询
     * @param sqlQuery SQL查询参数
     * @param rch 记录集处理器
     * @param stream 是否启用流式读取
     * @return 抽取结果
     */
    public <T> void query(SqlDao sqlQuery, IRowCallbackHandler rch, boolean stream);
    
    /**
     * 查询SQL
     * 
     * @param sql SQL
     * @param params 查询参数
     * @param rse 记录集抽取接口
     */
    public <T> List<T> query(String sql, IParameters params, IRowMapper<T> mapper);
    
    /**
     * 查询sql
     * 
     * @param sql SQL查询参数
     */
    public <T> List<T> query(SqlDao sqlQuery, IRowMapper<T> mapper);
    
    /**
     * 查询SQL
     * 
     * @param sql SQL
     * @param params 查询参数
     * @param rse 记录集抽取接口
     */
    public <T> T queryFirstRow(String sql, IParameters params, IRowMapper<T> mapper);
    
    /**
     * 查询sql
     * 
     * @param sql SQL查询参数
     */
    public <T> T queryFirstRow(SqlDao sqlQuery, IRowMapper<T> mapper);
    
}
