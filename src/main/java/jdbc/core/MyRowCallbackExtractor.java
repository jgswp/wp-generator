package jdbc.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import jdbc.IResultSetExtractor;
import jdbc.IRowMapper;


public class MyRowCallbackExtractor<T> implements IResultSetExtractor<List<T>> {
    private final static Logger dbLogger    = Logger.getLogger("YDDB");
    private final static int     WARN_ROWNUM = 20000;
    private final IRowMapper<T>  mapper;
    private final int            maxRowNum;
    
    public MyRowCallbackExtractor(IRowMapper<T> mapper) {
        this(mapper, 0);
    }
    
    public MyRowCallbackExtractor(IRowMapper<T> mapper, int maxRowNum) {
        this.mapper = mapper;
        this.maxRowNum = maxRowNum;
    }
    
    /** (non-Javadoc)
     */
    @Override
    public List<T> extractData(ResultSet rs) throws SQLException {
        int rowNum = 0;
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] columnNames = getColumnNames(rsmd, columnCount);
        List<T> list = new LinkedList<>();
        while (rs.next()) {
            T t = mapper.mapRow(rs, ++rowNum, columnNames, columnCount);
            list.add(t);
            
            if (rowNum >= maxRowNum && maxRowNum > 0) {
                break;
            }
            if (0 == (rowNum % WARN_ROWNUM)) {
                dbLogger.warn("extractData rowNum=" + rowNum);
            }
        }
        if (rowNum >= WARN_ROWNUM) {
            dbLogger.warn("extractData all rowNum=" + rowNum);
        }
        return list;
    }
    
    private String[] getColumnNames(ResultSetMetaData rsmd, int columnCount) throws SQLException {
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; ++i) {
            columnNames[i - 1] = mapper.getColumnName(rsmd, i);
        }
        return columnNames;
    }
    
}
