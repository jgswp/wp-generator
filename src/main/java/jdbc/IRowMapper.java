
package jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 行记录Map处理
 * @author wangpeng
 */
public interface IRowMapper<T> {
    /**
     * 获取列名
     * @param rsmd
     * @return 列名
     */
    String getColumnName(ResultSetMetaData rsmd, int index) throws SQLException;
    
    /**
     * * 处理一行记录
     * @param rs 
     * @param rowNum 行号
     * @param columnNames 列名
     * @param columnCount 列数
     * @return 转换后的对象
     * @throws SQLException
     */
    T mapRow(ResultSet rs, int rowNum, String[] columnNames, int columnCount) throws SQLException;
}
