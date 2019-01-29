package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 记录集合抽取器
 * @author wangpeng
 */
public interface IResultSetExtractor<T> {
    /**
     * 抽取数据
     * @param rs 记录集合
     * @return 数据集
     * @throws YDException
     */
    T extractData(ResultSet rs) throws SQLException;
}
