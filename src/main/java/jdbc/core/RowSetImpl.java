package jdbc.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import bean.YDException;
import jdbc.IRowSet;
import pubUtils.BeanUtils;
import pubUtils.ObjectUtils;
import pubUtils.StringHelper;

/**
 * 数据记录集，不支持BLOB字段
 * 
 * @author wangpeng
 * 
 */
public class RowSetImpl implements IRowSet {
    private SqlRowSet         rs            = null; // JdbcTemplate的RowSet
    private SqlRowSetMetaData rm            = null; // 查询语句的字段信息
    private String[]          propertyNames = null; // 对应字段属性名
    
    /**
     * 构造空记录集
     */
    RowSetImpl() {
        this(null);
    }
    
    /**
     * 构造记录集
     * 
     * @param rs
     */
    RowSetImpl(SqlRowSet rs) {
        this.rs = rs;
        if (rs != null) {
            this.rm = rs.getMetaData();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public int findColumn(String colName) {
        if (rs != null) {
            try {
                return rs.findColumn(colName);
            } catch (InvalidResultSetAccessException ignore) {
            }
        }
        return -1;
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public boolean next() {
        if (rs == null) {
            return false;
        }
        try {
            return rs.next();
        } catch (InvalidResultSetAccessException e) {
            throw new YDException("errMsg"+
                "游标错误！" + e.getMessage());
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public int getCol() {
        if (rs == null || rm == null) {
            return 0;
        }
        try {
            return rm.getColumnCount();
        } catch (InvalidResultSetAccessException e) {
            throw new YDException("errMsg"+
                "游标错误！" + e.getMessage());
        }
    }
    
    /**
     * Retrieves the designated column's SQL type.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return SQL type from java.sql.Types
     * @see Types
     */
    public int getColType(int column) {
        if (rs == null || rm == null) {
            return java.sql.Types.NULL;
        }
        try {
            return rm.getColumnType(column);
        } catch (InvalidResultSetAccessException e) {
            throw new YDException("errMsg"+
                "游标错误！" + e.getMessage());
        }
    }
    
    /**
     * Retrieves the designated column's SQL type.
     *
     * @return SQL type from java.sql.Types
     * @see Types
     */
    public int[] getColTypes() {
        if (rs == null || rm == null) {
            return new int[] {};
        }
        
        try {
            int col = rm.getColumnCount();
            int[] types = new int[col];
            for (int i = 1; i <= col; ++i) {
                types[i - 1] = rm.getColumnType(i);
            }
            return types;
        } catch (InvalidResultSetAccessException e) {
            throw new YDException("errMsg"+
                "游标错误！" + e.getMessage());
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public String[] getColumnNames() {
        if (rs == null || rm == null) {
            return null;
        }
        try {
            return rm.getColumnNames();
        } catch (InvalidResultSetAccessException e) {
            throw new YDException("errMsg"+
                "游标错误！" + e.getMessage());
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public String[] getPropertyNames() {
        if (propertyNames != null) {
            return propertyNames;
        }
        
        String[] columnNames = getColumnNames();
        if (columnNames != null && columnNames.length > 0) {
            propertyNames = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; ++i) {
                propertyNames[i] = StringHelper.columnName2propertyName(columnNames[i]);
            }
        }
        return propertyNames;
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public int getInt(int index) {
        Object obj = this.getObject(index);
        return ObjectUtils.toInt(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public int getInt(String colName) {
        Object obj = this.getObject(colName);
        return ObjectUtils.toInt(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public long getLong(int index) {
        Object obj = this.getObject(index);
        return ObjectUtils.toLong(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public long getLong(String colName) {
        Object obj = this.getObject(colName);
        return ObjectUtils.toLong(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public float getFloat(int index) {
        Object obj = this.getObject(index);
        return ObjectUtils.toFloat(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public float getFloat(String colName) {
        Object obj = this.getObject(colName);
        return ObjectUtils.toFloat(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public double getDouble(int index) {
        Object obj = this.getObject(index);
        return ObjectUtils.toDouble(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public double getDouble(String colName) {
        Object obj = this.getObject(colName);
        return ObjectUtils.toDouble(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public String getString(int index) {
        Object obj = this.getObject(index);
        return ObjectUtils.toString(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public String getString(String colName) {
        Object obj = this.getObject(colName);
        return ObjectUtils.toString(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public Date getDate(int index) {
        Object obj = this.getObject(index);
        return ObjectUtils.toDate(obj);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public Date getDate(String colName) {
        Object obj = this.getObject(colName);
        return ObjectUtils.toDate(obj);
    }
    
    /**
     * 获取index列值(blob)
     * @param index  第index列(从1开始)
     * @return 该字段的值(blob)
     */
    public byte[] getBlob(int index) {
        if (rs == null || rm == null) {
            return null;
        }
        return _loadBlob(index);
    }
    
    private byte[] _loadBlob(int index) {
        Object obj = null;
        try {
            obj = rs.getObject(index);
        } catch (InvalidResultSetAccessException e) {
            throw new YDException("errMsg"+
                "游标错误！" + e.getMessage());
        }
        
        if (obj == null) {
            return null;
        }
        if (obj.getClass() == byte[].class) {
            return (byte[]) obj;
        }
        if (obj instanceof java.sql.Blob) {
            java.sql.Blob blob = (java.sql.Blob) obj;
            try {
                int len = (int) blob.length();
                if (len <= 0) {
                    return new byte[0];
                }
                return blob.getBytes(1, len);
            } catch (SQLException e) {
                throw new YDException("获取的blob字段值异常"+e);
            }
        }
        //不是blob类型
        throw new YDException("获取的字段不是blob类型");
    }
    
    /**
     * 获取index列值(blob)
     * @param index  第index列(从1开始)
     * @return 该字段的值(blob)
     */
    public byte[] getBlob(String colName) {
        if (rs == null || rm == null) {
            return null;
        }
        int index = findColumn(colName);
        if (index < 0) {
            return null;
        }
        return _loadBlob(index);
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public Object getObject(int index) {
        if (rs == null || rm == null) {
            return null;
        }
        
        Object obj = null;
        try {
            obj = rs.getObject(index);
        } catch (InvalidResultSetAccessException e) {
            throw new YDException("errMsg"+
                "游标错误！" + e.getMessage());
        }
        
        if (obj == null) {
            return null;
        }
        
        if (java.util.Date.class.isAssignableFrom(obj.getClass())) {
            //java.sql.Date不支持dubbo序列化，统一改用java.util.Date
            java.util.Date dt = (java.util.Date) obj;
            return new java.util.Date(dt.getTime());
        }
        
        // Clob类型
        if (obj instanceof Clob) {
            Clob cb = (Clob) obj;
            try {
                obj = cb.getSubString(1, (int) cb.length());
            } catch (SQLException e) {
                obj = rs.getString(index);
            }
        }
        
        // 日期类型
        else if (obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP")) {
            try {
                Class<?> clz = obj.getClass();
                Method m = clz.getMethod("timestampValue");
                obj = (java.sql.Timestamp) m.invoke(obj);
            } catch (Throwable t) {
                throw new YDException("获取oracle的TIMESTAMP字段值异常!"+t);
            }
        } else if (obj.getClass().getName().startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rm.getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName)
                || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj instanceof Date
            && "java.sql.Timestamp".equals(rm.getColumnClassName(index))) {
            obj = rs.getTimestamp(index);
        }
        
        // 数值类型或字符串类型
        return obj;
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public Object getObject(String colName) {
        if (rs == null || rm == null) {
            return null;
        }
        int index = findColumn(colName);
        if (index < 0) {
            return null;
        }
        return this.getObject(index);
    }
    
    @Override
    public void saveToBean(Object bean, String prefix, boolean isParse) {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(bean.getClass());
        if (pds == null || pds.length <= 0) {
            return;
        }
        
        int colNum = getCol();
        int colIndex = -1;
        String colName = null;
        boolean isHavePrefix = StringHelper.isNotEmpty(prefix);
        Class<?> cls = null;
        for (PropertyDescriptor pd : pds) {
            cls = pd.getPropertyType();
            if (cls == Class.class) {
                continue;
            }
            
            colName = pd.getName();
            if (isParse) {
                colName = StringHelper.propertyName2ColumnName(colName);
            }
            
            if (isHavePrefix) {
                colName = prefix + colName;
            }
            
            colIndex = findColumn(colName);
            if (colIndex < 1 || colIndex > colNum) {
                continue;// 没有这列
            }
            
            if (cls == Long.class || cls == long.class) {// long类型
                BeanUtils.setPropertyValue(bean, pd, getLong(colIndex));
            } else if (cls == Double.class || cls == double.class) {// double类型
                BeanUtils.setPropertyValue(bean, pd, getDouble(colIndex));
            } else if (cls == Integer.class || cls == int.class) {// int类型
                BeanUtils.setPropertyValue(bean, pd, getInt(colIndex));
            } else if (cls == Float.class || cls == float.class) {// float类型
                BeanUtils.setPropertyValue(bean, pd, getFloat(colIndex));
            } else if (cls == String.class) {// String类型
                BeanUtils.setPropertyValue(bean, pd, getString(colIndex));
            } else if (Date.class.isAssignableFrom(cls)) {// 日期类型
                BeanUtils.setPropertyValue(bean, pd, getDate(colIndex));
            } else if (byte[].class == cls) {//blob类型
                BeanUtils.setPropertyValue(bean, pd, getBlob(colIndex));
            }
        }
    }
}
