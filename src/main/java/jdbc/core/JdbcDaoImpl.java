package jdbc.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import bean.MapBean;
import bean.YDException;
import jdbc.IJdbcDao;
import jdbc.INamedParameters;
import jdbc.IParameters;
import jdbc.IResultSetExtractor;
import jdbc.IRowCallbackHandler;
import jdbc.IRowMapper;
import jdbc.IRowSet;
import jdbc.SqlDao;
import jdbc.sequence.SequenceUtil;
import pubUtils.StringHelper;

/**
 * @author wangpeng
 */
public class JdbcDaoImpl extends BaseJdbcDao implements IJdbcDao {
    
    /** (non-Javadoc)
     */
    @Override
    public Date getSysDate() {
        String sql = dialect.buildDateSql();
        IRowSet rs = query(sql, null);
        if (rs.next()) {
            return rs.getDate(1);
        }
        String errMsg = "获取数据库时间失败";
        throw new YDException("errMsg"+errMsg);
    }
    
    /** (non-Javadoc)
     * @see com.eshore.crmpub.jdbc.IJdbcDao#getSeqID(java.lang.String)
     */
    @Override
    public long getSeqID() {
    	return SequenceUtil.getUUid();
    }
    
    /** (non-Javadoc)
     */
    @Override
    public int execSql(String sql, INamedParameters params) {
        return update(sql, params);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public int execSql(String sql, IParameters params) {
        return update(sql, params);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public IRowSet query(String sql, IParameters params) {
        return query(sql, params, new jdbc.core.ResultSetExtractor(), false);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public IRowSet query(SqlDao SqlDao) {
        String useSql = getUseSql(SqlDao);
        return query(useSql, SqlDao.getParamters(),
            new jdbc.core.ResultSetExtractor(), false);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public List<MapBean> queryForList(SqlDao SqlDao) {
//        String useSql = getUseSql(SqlDao);
//        return query(useSql, SqlDao.getParamters(),
//            new MyRowCallbackExtractor<MapBean>(ColumnMapRowMapper.getInstance()), false);
    	return null;
    }
    
    /** (non-Javadoc)
     */
    @Override
    public int[] batchExecSql(String sql, List<?> params) {
        return batchUpdate(sql, params);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> T query(String sql, IParameters params, IResultSetExtractor<T> rse, boolean stream) {
        return super.query(sql, params, rse, stream);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> T query(SqlDao SqlDao, IResultSetExtractor<T> rse, boolean stream) {
        String useSql = getUseSql(SqlDao);
        return query(useSql, SqlDao.getParamters(), rse, stream);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> void query(String sql, IParameters params, IRowCallbackHandler rch, boolean stream) {
        query(sql, params, new RowCallbackExtractor(rch), stream);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> void query(SqlDao SqlDao, IRowCallbackHandler rch, boolean stream) {
        String useSql = getUseSql(SqlDao);
        query(useSql, SqlDao.getParamters(), new RowCallbackExtractor(rch), stream);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> List<T> query(String sql, IParameters params, IRowMapper<T> mapper) {
        return query(sql, params, new MyRowCallbackExtractor<T>(mapper), false);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> List<T> query(SqlDao SqlDao, IRowMapper<T> mapper) {
        String useSql = getUseSql(SqlDao);
        return query(useSql, SqlDao.getParamters(), new MyRowCallbackExtractor<T>(mapper), false);
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> T queryFirstRow(String sql, IParameters params, IRowMapper<T> mapper) {
    	Collection<T> collection = query(sql, params, new MyRowCallbackExtractor<T>(mapper, 1), false);
    	//Collection<T> collection = new ArrayList<T>();
    	return collection == null || collection.isEmpty() ? null
	            : collection.iterator().next();
    }
    
    /** (non-Javadoc)
     */
    @Override
    public <T> T queryFirstRow(SqlDao SqlDao, IRowMapper<T> mapper) {
        String useSql = getUseSql(SqlDao);
//        return CollectionHelper.getFirst(query(useSql, SqlDao.getParamters(),
//            new MyRowCallbackExtractor<T>(mapper, 1), false));
        return null;
    }
    
    private String getUseSql(SqlDao SqlDao) {
        String sql = SqlDao.getSql();
        if (StringHelper.isNotEmpty(SqlDao.getHint())) {
            sql = new StringBuilder(SqlDao.getHint()).append(SqlDao.getSql()).toString();
        }
        return sql;
    }
    
}
