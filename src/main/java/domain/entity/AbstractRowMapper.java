package domain.entity;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.support.JdbcUtils;

import jdbc.IRowMapper;

public abstract class AbstractRowMapper<T> implements IRowMapper<T> {
    @Override
    public String getColumnName(ResultSetMetaData rsmd, int index) throws SQLException {
        return JdbcUtils.lookupColumnName(rsmd, index);
    }
    
    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        Object value = JdbcUtils.getResultSetValue(rs, index);
        return transObject(value, null);
    }
    
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd)
        throws SQLException {
        Object value = JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
        return transObject(value, pd.getPropertyType());
    }
    
    private Object transObject(Object value, Class<?> requiredType) {
        if (value == null) {
            return null;
        }
        
        if (java.util.Date.class.isAssignableFrom(value.getClass())) {
            if (requiredType == null || java.util.Date.class == requiredType) {
                //java.sql.Date不支持dubbo序列化，统一改用java.util.Date
                java.util.Date dt = (java.util.Date) value;
                return new java.util.Date(dt.getTime());
            }
        }
        
        return value;
    }
}
