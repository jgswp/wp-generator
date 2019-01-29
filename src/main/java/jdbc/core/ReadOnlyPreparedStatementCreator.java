package jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.SqlProvider;

/**
 * @author wangpeng
 */
public class ReadOnlyPreparedStatementCreator
    implements org.springframework.jdbc.core.PreparedStatementCreator, SqlProvider {
    private final String sql;
    
    public ReadOnlyPreparedStatementCreator(String sql) {
        this.sql = sql;
    }
    
    /** (non-Javadoc)
     * @see org.springframework.jdbc.core.PreparedStatementCreator#createPreparedStatement(java.sql.Connection)
     */
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(this.sql, ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
        ps.setFetchSize(Integer.MIN_VALUE);
        return ps;
    }
    
    /** (non-Javadoc)
     * @see org.springframework.jdbc.core.SqlProvider#getSql()
     */
    public String getSql() {
        return sql;
    }
    
}
