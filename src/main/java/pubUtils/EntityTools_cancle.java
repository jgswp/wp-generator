package pubUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EntityTools_cancle {
	//基本数据配置  
    private String packageOutPath = "entity";// 指定实体生成所在包的路径  
    private String authorName = "system";// 作者名字  
    private static String tablename = "";// 表名  
    private String[] colnames; // 列名数组  
    private String[] colTypes; // 列名类型数组  
    private String version = "V0.01"; // 版本  
    private int[] colSizes; // 列名大小数组  
    private boolean f_util = false; // 是否需要导入包java.util.*  
    private boolean f_sql = false; // 是否需要导入包java.sql.*  
    private boolean f_lang = false; // 是否需要导入包java.sql.*  
    private String defaultPath = "/src/main/java/";  
    // 数据库连接  
    private static final String URL = PropertiesUtil.getProperty("url"); 
    private static final String NAME = PropertiesUtil.getProperty("username");   
    private static final String PASS = PropertiesUtil.getProperty("password");   
    private static final String DRIVER ="com.mysql.jdbc.Driver";  
  
    /* 
     * 构造函数 
     */  
    public EntityTools_cancle() {  
        // 创建连接  
        Connection con;  
        // 查要生成实体类的表  
        String sql = "select 1 from " + tablename;  
        PreparedStatement pStemt = null;  
        try {  
            try {  
                Class.forName(DRIVER);  
            } catch (ClassNotFoundException e1) {  
                // TODO Auto-generated catch block  
                e1.printStackTrace();  
            }  
            con = DriverManager.getConnection(URL, NAME, PASS);  
            pStemt = con.prepareStatement(sql);  
            ResultSetMetaData rsmd = pStemt.getMetaData();  
            int size = rsmd.getColumnCount(); // 统计列  
            colnames = new String[size];  
            colTypes = new String[size];  
            colSizes = new int[size];  
            for (int i = 0; i < size; i++) {  
  
                colnames[i] = rsmd.getColumnName(i + 1);  
                colTypes[i] = rsmd.getColumnTypeName(i + 1);  
                //自动生成包配置  
                  
                  
                // if (colTypes[i].equalsIgnoreCase("datetime")) {  
                // f_util = true;  
                // }  
                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")  
                        || colTypes[i].equalsIgnoreCase("datetime") || colTypes[i].equalsIgnoreCase("time")  
                        || colTypes[i].equalsIgnoreCase("date") || colTypes[i].equalsIgnoreCase("datetime2")) {  
                    f_sql = true;  
                }  
                // if (colTypes[i].equalsIgnoreCase("int")) {  
                // f_lang = true;  
                // }  
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);  
            }  
  
            String content = parse(colnames, colTypes, colSizes);  
  
            try {  
                File directory = new File("");  
                  
                String path = this.getClass().getResource("").getPath();  
  
                System.out.println(path);  
                  
                String outputPath = directory.getAbsolutePath() + this.defaultPath  
                        + this.packageOutPath.replace(".", "/") + "/" + initcap(tablename) + ".java";  
                System.out.println("执行完毕，生成路径为："+outputPath);  
                FileWriter fw = new FileWriter(outputPath);  
                PrintWriter pw = new PrintWriter(fw);  
                pw.println(content);  
                pw.flush();  
                pw.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
  
        } catch (SQLException e) {  
            e.printStackTrace();  
        } finally {  
              
        }  
    }  
  
    /** 
     * 功能：生成实体类主体代码 
     *  
     * @param colnames 
     * @param colTypes 
     * @param colSizes 
     * @return 
     */  
    private String parse(String[] colnames, String[] colTypes, int[] colSizes) {  
        StringBuffer sb = new StringBuffer();  
        // 生成package包路径  
        sb.append("package " + this.packageOutPath + ";\r\n");  
        // 判断是否导入工具包  
        if (f_util) {  
            sb.append("import java.util.Date;\r\n");  
        }  
        if (f_sql) {  
            sb.append("import java.sql.*;\r\n");  
        }  
        if (f_lang) {  
            sb.append("import java.lang.*;\r\n");  
        }  
  
        sb.append("\r\n");  
        // 注释部分  
        sb.append("   /**\r\n");  
        sb.append("    * @文件名称：" + this.tablename + ".java\r\n");  
        sb.append("    * @创建时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\r\n");  
        sb.append("    * @创  建  人：" + this.authorName + " \r\n");  
        sb.append("    * @文件描述：" + tablename + " 实体类\r\n");  
        sb.append("    * @文件版本：" + this.version + " \r\n");  
        sb.append("    */ \r\n");  
        // 实体部分  
        sb.append("\r\n\r\npublic class " + initcap(tablename) + "{\r\n");  
        processAllAttrs(sb);// 属性  
        processAllMethod(sb);// get set方法  
        sb.append("}\r\n");  
  
        // System.out.println(sb.toString());  
        return sb.toString();  
    }  
  
    /** 
     * 功能：生成所有属性 
     *  
     * @param sb 
     */  
    private void processAllAttrs(StringBuffer sb) {  
  
        for (int i = 0; i < colnames.length; i++) {  
            sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ";\r\n");  
        }  
  
    }  
  
    /** 
     * 功能：生成所有方法 
     *  
     * @param sb 
     */  
    private void processAllMethod(StringBuffer sb) {  
  
        for (int i = 0; i < colnames.length; i++) {  
            sb.append("\tpublic void set" + initcap(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " "  
                    + colnames[i] + "){\r\n");  
            sb.append("\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");  
            sb.append("\t}\r\n");  
            sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(colnames[i]) + "(){\r\n");  
            sb.append("\t\treturn " + colnames[i] + ";\r\n");  
            sb.append("\t}\r\n");  
        }  
  
    }  
  
    /** 
     * 功能：将输入字符串的首字母改成大写 
     *  
     * @param str 
     * @return 
     */  
    private String initcap(String str) {  
  
        char[] ch = str.toCharArray();  
        if (ch[0] >= 'a' && ch[0] <= 'z') {  
            ch[0] = (char) (ch[0] - 32);  
        }  
  
        return new String(ch);  
    }  
  
    /** 
     * 功能：获得列的数据类型 
     *  
     * @param sqlType 
     * @return 
     */  
    private String sqlType2JavaType(String sqlType) {  
  
        if (sqlType.equalsIgnoreCase("bit")) {  
            return "Boolean";  
        } else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("money")  
                || sqlType.equalsIgnoreCase("smallmoney") || sqlType.equalsIgnoreCase("numeric")  
                || sqlType.equalsIgnoreCase("bigint")) {  
            return "Long";  
        } else if (sqlType.equalsIgnoreCase("float")) {  
            return "Double";  
        } else if (sqlType.equalsIgnoreCase("int") || sqlType.equalsIgnoreCase("int identity")) {  
            return "Integer";  
        } else if (sqlType.equalsIgnoreCase("image") || sqlType.equalsIgnoreCase("varbinary(max)")  
                || sqlType.equalsIgnoreCase("varbinary") || sqlType.equalsIgnoreCase("udt")  
                || sqlType.equalsIgnoreCase("timestamp") || sqlType.equalsIgnoreCase("binary")) {  
            return "Byte[]";  
        } else if (sqlType.equalsIgnoreCase("nchar") || sqlType.equalsIgnoreCase("nvarchar(max)")  
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nvarchar(ntext)")  
                || sqlType.equalsIgnoreCase("uniqueidentifier") || sqlType.equalsIgnoreCase("xml")  
                || sqlType.equalsIgnoreCase("char") || sqlType.equalsIgnoreCase("varchar(max)")  
                || sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("varchar")) {  
            return "String";  
        } else if (sqlType.equalsIgnoreCase("real")) {  
            return "Float";  
        } else if (sqlType.equalsIgnoreCase("smallint") || sqlType.equalsIgnoreCase("tinyint")) {  
            return "Short";  
        } else if (sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("datetime")  
                || sqlType.equalsIgnoreCase("time") || sqlType.equalsIgnoreCase("datetime2")) {  
            return "Date";  
        } else {  
            System.out.println("数据类型异常，类型为：" + sqlType);  
        }  
  
        return null;  
    }  
  
    /** 
     * 出口 TODO 
     *  
     * @param args 
     */  
    public static void main(String[] args) { 
        String tables = PropertiesUtil.getProperty("password"); 

    	 for (String tableName : StringHelper.splitTokens(tables, ",")) {
    		 tablename = tableName;
    		 new EntityTools_cancle();  
    	 }
  
    }  
}
