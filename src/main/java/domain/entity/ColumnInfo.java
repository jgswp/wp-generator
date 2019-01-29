package domain.entity;

import java.beans.PropertyDescriptor;

import bean.ColumnType;

/**
 * 字段信息
 * 
 * @author wangpeng
 *
 */
public class ColumnInfo {
    private String             colName;   // 字段名
    private String             propName;  // 属性名
    private ColumnType         colType;   // 字段类型
    private Class<?>           classType; // 字段java类型
    private int                index;     // 排序号
    private PropertyDescriptor pd;        // 属性
    
    /**
     * @return the colName
     */
    public String getColName() {
        return colName;
    }
    
    /**
     * @param colName the colName to set
     */
    public void setColName(String colName) {
        this.colName = colName;
    }
    
    /**
     * @return the propName
     */
    public String getPropName() {
        return propName;
    }
    
    /**
     * @param propName the propName to set
     */
    public void setPropName(String propName) {
        this.propName = propName;
    }
    
    /**
     * @return the colType
     */
    public ColumnType getColType() {
        return colType;
    }
    
    /**
     * @param colType the colType to set
     */
    public void setColType(ColumnType colType) {
        this.colType = colType;
    }
    
    /**
     * @return the classType
     */
    public Class<?> getClassType() {
        return classType;
    }
    
    /**
     * @param classType the classType to set
     */
    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }
    
    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * @return the pd
     */
    public PropertyDescriptor getPd() {
        return pd;
    }
    
    /**
     * @param pd the pd to set
     */
    public void setPd(PropertyDescriptor pd) {
        this.pd = pd;
    }
    
}
