package jdbc.freemarker;

import pubUtils.PropertiesUtil;
import pubUtils.StringHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReflectBean {
    private Connection connection;
    //数据库中的表名
    private String table;
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    //数据库的列名称
    private String[] colnames; // 列名数组
    //列名类型数组
    private String[] colTypes;

    public String[] getColnames() {
        return colnames;
    }

    public void setColnames(String[] colnames) {
        this.colnames = colnames;
    }

    public String[] getColTypes() {
        return colTypes;
    }

    public void setColTypes(String[] colTypes) {
        this.colTypes = colTypes;
    }

    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ReflectBean(String url,String username,String password,String driverClassName){
        try {//驱动注册

            Class.forName(driverClassName);
            if (connection == null || connection.isClosed())
                //获得链接
            {
                connection = DriverManager.getConnection(url, username, password);
            }



        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Oh,not");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Oh,not");
        }
    }



    public List<BeanModel> doAction(){
        String sql = "select * from "+table;
        List<String> list = new ArrayList<String>();
        List<BeanModel> beanModelslist = new ArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            //获取数据库的元数据
            ResultSetMetaData metadata = statement.getMetaData();
            ResultSet rs = connection.getMetaData().getPrimaryKeys(null, null, table);
            String id = "";
            //获取组键字段
            if(rs.next()){
                id = rs.getString(4);
            }
            //数据库的字段个数
            int len = metadata.getColumnCount();
            //字段名称
            colnames = new String[len+1];
            //字段类型 --->已经转化为java中的类名称了
            colTypes = new String[len+1];
            for(int i= 1;i<=len;i++){
                BeanModel beanModel = new BeanModel();

                //System.out.println(metadata.getColumnName(i)+":"+metadata.getColumnTypeName(i)+":"+sqlType2JavaType(metadata.getColumnTypeName(i).toLowerCase())+":"+metadata.getColumnDisplaySize(i));
                //metadata.getColumnDisplaySize(i);
                colnames[i] = metadata.getColumnName(i); //获取字段名称
                String s = getClassName(colnames[i]);
                beanModel.setCloumName(colnames[i]);
                beanModel.setJavaName(s);
                colTypes[i] = metadata.getColumnTypeName(i); //获取字段类型
                if("INT".equals( colTypes[i] )){
                    colTypes[i]="INTEGER";
                }
                if("DATETIME".equals(colTypes[i])){
                    colTypes[i]="TIMESTAMP";
                }
                beanModel.setCloumType(colTypes[i]);
                String s1 = sqlType2JavaType(colTypes[i]);
                beanModel.setJavaType(s1);
                beanModelslist.add(beanModel);
            }
            list.add(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beanModelslist;
    }

    /*
     * mysql的字段类型转化为java的类型*/
    private String sqlType2JavaType(String sqlType) {

        if(sqlType.equalsIgnoreCase("bit")){
            return "Boolean";
        }else if(sqlType.equalsIgnoreCase("tinyint")){
            return "Byte";
        }else if(sqlType.equalsIgnoreCase("smallint")){
            return "Short";
        }else if(sqlType.equalsIgnoreCase("int")|| sqlType.equalsIgnoreCase("INTEGER")){
            return "Integer";
        }else if(sqlType.equalsIgnoreCase("bigint")){
            return "Long";
        }else if(sqlType.equalsIgnoreCase("float")){
            return "Float";
        }else if(sqlType.equalsIgnoreCase("decimal")){
            return "BigDecimal";
        }
        else if( sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")){
            return "double";
        }else if(sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text")){
            return "String";
        }else if(sqlType.equalsIgnoreCase("datetime") ||sqlType.equalsIgnoreCase("TIMESTAMP")
                ||(sqlType.equalsIgnoreCase("DATE")) || (sqlType.equalsIgnoreCase("year"))
                ){
            return "Date";
        }else if(sqlType.equalsIgnoreCase("image")){
            return "Blod";
        }else if(sqlType.equalsIgnoreCase("timestamp")){
            return "Timestamp";
        }

        return null;
    }
    /*获取整个类的字符串并且输出为java文件
     * */
    public  StringBuffer getClassStr(){
        //输出的类字符串
        StringBuffer str = new StringBuffer("");
        //获取表类型和表名的字段名
        this.doAction();
        //校验
        if(null == colnames && null == colTypes) return null;
        //拼接
        str.append("public class "+GetTuoFeng(table)+" {\r\n");
        //拼接属性
        for(int index=1; index < colnames.length ; index++){
            str.append(getAttrbuteString(colnames[index],colTypes[index]));
        }
        //拼接get，Set方法
        for(int index=1; index < colnames.length ; index++){
            str.append(getGetMethodString(colnames[index],colTypes[index]));
            str.append(getSetMethodString(colnames[index],colTypes[index]));
        }
        str.append("}\r\n");
        //输出到文件中
        File file = new File("E:/mengwx/【源码】mysql版本_spring4.0/FHMYSQL/src/com/fh/entity/"+GetTuoFeng(table)+".java");
        BufferedWriter write = null;

        try {
            write = new BufferedWriter(new FileWriter(file));
            write.write(str.toString());
            write.close();
        } catch (IOException e) {

            e.printStackTrace();
            if (write != null)
                try {
                    write.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
        return str;
    }
    /*
     * 获取字段字符串*/
    public StringBuffer getAttrbuteString(String name, String type) {
        if(!check(name,type)) {
            System.out.println("类中有属性或者类型为空");
            return null;
        };
        String format = String.format("    private %s %s;\n\r", new String[]{type,name});
        return new StringBuffer(format);
    }
    /*
     * 校验name和type是否合法*/
    public boolean check(String name, String type) {
        if("".equals(name) || name == null || name.trim().length() ==0){
            return false;
        }
        if("".equals(type) || type == null || type.trim().length() ==0){
            return false;
        }
        return true;

    }
    /*
     * 获取get方法字符串*/
    private StringBuffer getGetMethodString(String name, String type) {
        if(!check(name,type)) {
            System.out.println("类中有属性或者类型为空");
            return null;
        };
        String Methodname = "get"+GetTuoFeng(name);
        String format = String.format("    public %s %s(){\n\r", new Object[]{type,Methodname});
        format += String.format("        return this.%s;\r\n", new Object[]{name});
        format += "    }\r\n";
        return new StringBuffer(format);
    }
    //将名称首字符大写
    private String GetTuoFeng(String name) {
        name = name.trim();
        if(name.length() > 1){
            name = name.substring(0, 1).toUpperCase()+name.substring(1);
        }else
        {
            name = name.toUpperCase();
        }
        return name;
    }
    /*
     * 获取字段的get方法字符串*/
    private Object getSetMethodString(String name, String type) {
        if(!check(name,type)) {
            System.out.println("类中有属性或者类型为空");
            return null;
        };
        String Methodname = "set"+GetTuoFeng(name);
        String format = String.format("    public void %s(%s %s){\n\r", new Object[]{Methodname,type,name});
        format += String.format("        this.%s = %s;\r\n", new Object[]{name,name});
        format += "    }\r\n";
        return new StringBuffer(format);
    }

    public static void main(String[] args) {
       // ReflectBean bean = new ReflectBean();
      //  System.err.println(bean.getClassStr());
    }
    /**
     *
     * @Title: changeToJavaFiled
     * @Description: TODO(将数据库中带下划线的字段转换为Java常用的驼峰字段)
     * @param @param field
     * @param @return    设定文件
     * @return String    返回类型
     * @author ll-t150
     * @date 2017年11月17日 下午12:11:59
     * @throws
     */

    public static String getClassName(String tableName) {

        StringBuilder sb = new StringBuilder();
        String[] tabs = tableName.split("_");
        for (int i = 0; i <tabs.length ; i++) {
            if (StringHelper.isEmpty(tabs[i])|| tabs.length==1) {
                return tabs[i];
            }
            String s = Character.toUpperCase(tabs[i].charAt(0)) + tabs[i].substring(1);
            if(i==0){
                String first = s.substring(0, 1).toLowerCase();
                String rest = s.substring(1, s.length());
                s = new StringBuffer(first).append(rest).toString();    }

            sb.append(s);
        }
     /*   for (String s : tabs) {
            if (StringHelper.isEmpty(s)|| tabs.length==1) {
                return s;
            }
            sb.append( Character.toUpperCase(s.charAt(0)) + s.substring(1));
        }*/
        return sb.toString();
    }
}
