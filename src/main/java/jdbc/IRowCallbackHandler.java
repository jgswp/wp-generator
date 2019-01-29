package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 记录集处理器
 * @author wangpeng
 */
public interface IRowCallbackHandler {
    /**
     * 处理一行记录
     * @param rs
     * @throws YDException
     */
    void processRow(ResultSet rs) throws SQLException;
}
