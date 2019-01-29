package jdbc.freemarker;




public class BeanModel {
    private String cloumName;//数据库列名
    private  String cloumType;//数据库类型
    private  String  javaName;//属性名
    private  String  javaType;//属性名

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getCloumName() {
        return cloumName;
    }

    public void setCloumName(String cloumName) {
        this.cloumName = cloumName;
    }

    public String getCloumType() {
        return cloumType;
    }

    public void setCloumType(String cloumType) {
        this.cloumType = cloumType;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }
}
