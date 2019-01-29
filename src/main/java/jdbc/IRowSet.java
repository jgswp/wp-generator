package jdbc;

import java.sql.Types;
import java.util.Date;

/**
 * 记录集
 * 
 * @author wangpeng
 *
 */
public interface IRowSet {
    
    /**
     * 查找字段名对应的字段位置
     * 
     * @param colName
     *            字段名
     * @return 字段位置
     */
    int findColumn(String colName);
    
    /**
     * 读取下一条记录
     * 
     * @return 如果有记录就返回true,没有就返回false
     */
    boolean next();
    
    /**
     * 获取列数
     * 
     * @return 返回列数
     */
    int getCol();
    
    /**
     * Retrieves the designated column's SQL type.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return SQL type from java.sql.Types
     * @see Types
     */
    int getColType(int column);
    
    /**
     * Retrieves the designated column's SQL type.
     *
     * @return SQL type from java.sql.Types
     * @see Types
     */
    int[] getColTypes();
    
    /**
     * 获取SQL查询的字段
     * 
     * @return 字段名数组
     */
    String[] getColumnNames();
    
    /**
     * 获取SQL查询字段对应的属性名
     * 
     * @return 属性名数组
     */
    String[] getPropertyNames();
    
    /**
     * 获取index列值(int)
     * 
     * @param index
     *            第index列(从1开始)
     * @return 该列的int值
     */
    int getInt(int index);
    
    /**
     * 获取colName字段值(int)
     * 
     * @param colName
     *            字段名
     * @return 该字段的int值
     */
    int getInt(String colName);
    
    /**
     * 获取index列值(long)
     * 
     * @param index
     *            第index列(从1开始)
     * @return 该列的long值
     */
    long getLong(int index);
    
    /**
     * 获取colName字段值(long)
     * 
     * @param colName
     *            字段名
     * @return 该字段的long值
     */
    long getLong(String colName);
    
    /**
     * 获取index列值(float)
     * 
     * @param index
     *            第index列(从1开始)
     * @return 该列的float值
     */
    float getFloat(int index);
    
    /**
     * 获取colName字段值(float)
     * 
     * @param colName
     *            字段名
     * @return 该字段的float值
     */
    float getFloat(String colName);
    
    /**
     * 获取index列值(double)
     * 
     * @param index
     *            第index列(从1开始)
     * @return 该列的double值
     */
    double getDouble(int index);
    
    /**
     * 获取colName字段值(double)
     * 
     * @param colName
     *            字段名
     * @return 该字段的double值
     */
    double getDouble(String colName);
    
    /**
     * 获取index列值(string)
     * 
     * @param index
     *            第index列(从1开始)
     * @return 该字段的string值
     */
    String getString(int index);
    
    /**
     * 获取index列值(string)
     * 
     * @param colName
     *            字段名
     * @return 该字段的string值
     */
    String getString(String colName);
    
    /**
     * 获取index列值(date)
     * 
     * @param index
     *            第index列(从1开始)
     * @return 该字段的date值
     */
    Date getDate(int index);
    
    /**
     * 获取colName字段值(date)
     * 
     * @param colName
     *            字段名
     * @return 该字段的date值
     */
    Date getDate(String colName);
    
    /**
     * 获取index列值(blob)
     * @param index  第index列(从1开始)
     * @return 该字段的值(blob)
     */
    byte[] getBlob(int index);
    
    /**
     * 获取index列值(blob)
     * @param index  第index列(从1开始)
     * @return 该字段的值(blob)
     */
    byte[] getBlob(String colName);
    
    /**
     * 获取index列值(object)
     * 
     * @param index
     *            第index列(从1开始)
     * @return Object 该字段的值(object)
     */
    Object getObject(int index);
    
    /**
     * 获取字段值(object)
     * 
     * @param colName
     *            字段名
     * @return 该字段的值(object)
     */
    Object getObject(String colName);
    
    /**
     * 读取记录集输出到bean
     * 
     * @param bean
     *            javaBean对象
     * @param prefix
     *            字段别名前缀
     * @param isParse
     *            是否按照字段属性名对应输出
     */
    void saveToBean(Object bean, String prefix, boolean isParse);
}
