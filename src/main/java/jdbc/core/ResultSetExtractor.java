package jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import jdbc.IResultSetExtractor;
import jdbc.IRowSet;

/**
 * @author wangpeng
 */
public class ResultSetExtractor implements IResultSetExtractor<IRowSet> {
    
    /** (non-Javadoc)
     */
    @Override
    public IRowSet extractData(ResultSet rs) throws SQLException {
        SqlRowSetResultSetExtractor rse = new SqlRowSetResultSetExtractor();
        SqlRowSet sqlRowSet = rse.extractData(DelegatingResultSet.wrapResultSet(rs));
        return new RowSetImpl(sqlRowSet);
    }
    
}
