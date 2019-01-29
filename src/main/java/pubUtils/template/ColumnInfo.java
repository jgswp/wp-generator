package pubUtils.template;

import java.util.HashSet;
import java.util.Set;

import pubUtils.StringHelper;

public class ColumnInfo {
    private final static Set<String> javaKeyWords = new HashSet<String>();
    private String                   columnName;                          // 字段名
    private String                   propertiesName;                      // 属性名
    private String                   dataType;                            // 字段类型
    private String                   javaType;                            // 字段类型
    private String                   colType;                             // 字段类型
    private String                   comment;                             // 字段描述
    private int                      decimalDigits;                       // 小数字数
    private String                   defValue     = "null";               //默认值
    
    public String getColumnName() {
        return columnName;
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    public String getPropertiesName() {
        return propertiesName;
    }
    
    public void setPropertiesName(String propertiesName) {
        this.propertiesName = propertiesName;
        if (javaKeyWords.contains(propertiesName)) {
            this.propertiesName = "_" + propertiesName;
        }
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getJavaType() {
        return javaType;
    }
    
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
    
    /**
     * @return the colType
     */
    public String getColType() {
        return colType;
    }
    
    /**
     * @param colType the colType to set
     */
    public void setColType(String colType) {
        this.colType = colType;
    }
    
    public boolean isPK() {
        return "PK".equals(colType);
    }
    
    public boolean isShard() {
        return "SHARD".equals(colType);
    }
    
    public boolean isShardUnused() {
        return "SHARD_UNUSED".equals(colType);
    }
    
    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }
    
    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * @return the decimalDigits
     */
    public int getDecimalDigits() {
        return decimalDigits;
    }
    
    /**
     * @param decimalDigits the decimalDigits to set
     */
    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }
    
    public String getDefValue() {
        return defValue;
    }
    
    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }
    
    static {
        String keyWords = "abstract,assert,boolean,break,byte,case,catch,char,class,const,continue,default,do,double,else,enum,extends,final,finally,float,for,goto,if,implements,import,instanceof,int,interface,long,native,new,package,private,protected,public,return,short,static,strictfp,super,switch,synchronized,this,throw,throws,transient,try,void,volatile,while";
        javaKeyWords.addAll(StringHelper.splitToSet(keyWords, ","));
    }
    
}
