package pubUtils;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


import jdbc.freemarker.CreateCodeUtil;
import bean.MapBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jdbc.freemarker.ReflectBean;
import pubUtils.template.ColumnInfo;
import pubUtils.template.First2Lower;
import pubUtils.template.First2Upper;
import pubUtils.template.TabNameFormat;
import pubUtils.template.TableInfo;

public class JdbcTools {
    private static File         templateFile     = null;
    private static String       templateFileName = "template";
    private static Properties   properties       = new Properties();
    
    //类型
    private static String       INTEGER_TYPE     = "Integer";
    private static String       LONG_TYPE        = "Long";
    private static String       DOUBLE_TYPE      = "Double";
    private static String       STRING_TYPE      = "String";
    private static String       DATE_TYPE        = "Date";
    private static String       BINARY_TYPE      = "byte[]";
    private static String       FlOAT_TYPE      = "Float";
    private static String       DECIMAL_TYPE      = "BigDecimal";


    public static void main(String[] args) {
        String mapperJavaPath ="C:\\Users\\Administrator\\Desktop\\pub-dao\\src\\main\\java\\test";
        String mapperXmlPath="E:\\app\\";
        String entityPath="E:\\app\\";
        String tables = "btest";
        String dbDriver= "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/shoppingmall?useUnicode=true&characterEncoding=UTF-8";
        String user="root";
        String password = "123";
        String owner ="root";

         create(tables,dbDriver,url,user,password,owner,mapperJavaPath,mapperJavaPath,mapperJavaPath);
    }

    public static void create(String tables ,String dbDriver,String url,
                              String user,String password ,String  owner,String mapperPath,String mapperXmlPath,String entityPath ) {
    	System.out.println("生成entity类开始.......");
        try {
        } catch (Exception ex) {
            System.exit(-1);
        }
        
        try {
           // String tables = PropertiesUtil.getProperty("tables");
            if (StringHelper.isNotEmpty(tables)) {
                if ("all".equalsIgnoreCase(tables)) {//全部表
                   //暂不支持所有表
                } else {//指定某些表
                    for (String tableName : StringHelper.splitTokens(tables, ",")) {
                        //生成java实体类
                        Map<String, String> stringStringMap = genTableEntity(tableName, dbDriver, url, user, password, owner, entityPath);

                        String entityClassName = stringStringMap.get("entityClassName");
                        String packageName = stringStringMap.get("packageName");
                        //生成mapper.xml文件
                        //生成mapper.java文件（兼容low B 们）
                        String nameSpace = genEntityMapperJava(packageName,entityClassName, tableName,url,user,password,dbDriver,mapperPath);
                        genEntityMapperXml(entityClassName,tableName,nameSpace,url,user,password,dbDriver,  mapperXmlPath);

                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
        }
    }

    private static String genEntityMapperJava(String pack, String s, String tableName,
             String url,String username,String password,String driverClassName,String mapperPath) throws Exception {

        return  CreateCodeUtil.proCodeMapper(pack, s, tableName,url,username,password,driverClassName,mapperPath);
    }

    private static void genEntityMapperXml(String s, String tableName,String nameSpace,
                                           String url,String username,String password,String driverClassName
                                           ,String mapperXmlPath) throws Exception {
        CreateCodeUtil.proCode(s,tableName,nameSpace,url,username,password,driverClassName,mapperXmlPath);
    }

    private static Map<String, String> genTableEntity(String tabName, String dbDriver, String url,
                                                      String user, String password , String  owner, String entityPath) {
        
        List<ColumnInfo> allCols = new ArrayList<ColumnInfo>();
        Map<String, ColumnInfo> mapCols = new LinkedHashMap<String, ColumnInfo>();
        Map<String,String>map = new HashMap<>();
        String lowerTableName = tabName.toLowerCase();
        String upperTableName = tabName.toUpperCase();
        
        //配置指定的PK
        String pkStr = null;
        Set<String> pks = StringHelper.splitToSet(pkStr == null ? null
            : pkStr.toUpperCase(), ", ");
        
        Connection conn = null;
        int dateColNum = 0;
        int decimalColNum = 0;
        int pkNum = 0;
        int shardNum = 0;
    	//String dbDriver="com.mysql.jdbc.Driver";
	    //声明数据源
	 /*   String url=PropertiesUtil.getProperty("url");
	    String user = PropertiesUtil.getProperty("username");
	    String password = PropertiesUtil.getProperty("password");
	    String owner = PropertiesUtil.getProperty("owner");*/
	    //连接数据库
        try {
        	Class.forName(dbDriver);
        	conn=(Connection) DriverManager.getConnection(url,user,password);
        	
            DatabaseMetaData dmd = conn.getMetaData();
            
            //获取主键字段
            boolean hasId = false;

            ResultSet rs = dmd.getPrimaryKeys(null, null, tabName);
            while (rs.next()) {
                String column = rs.getString("column_name").toUpperCase();
                if ("ID".equals(column)) {
                    hasId = true;
                    continue;
                }
                pks.add(column);
            }
            rs.close();
            
            if (pks.isEmpty()) {
                //获取唯一索引
                rs = dmd.getIndexInfo(null, owner, tabName, true, false);
                while (rs.next()) {
                    if (rs.getString("column_name") == null) {
                        continue;
                    }
                    String column = rs.getString("column_name").toUpperCase();
                    if ("ID".equals(column)) {
                        hasId = true;
                        continue;
                    }
                    pks.add(column);
                }
                rs.close();
            }
            
            boolean needId = false;
            if (pks.isEmpty() && hasId) {
                needId = true;
                pks.add("ID");
            }
            
            rs = dmd.getColumns(null, owner, tabName, null);
            while (rs.next()) {
            	ColumnInfo col = new ColumnInfo();
                col.setColumnName(rs.getString("column_name").toUpperCase());
                col.setPropertiesName(ReflectBean.getClassName(rs.getString("column_name")));
                col.setDataType(rs.getString("type_name").toUpperCase());
                col.setColType("COL");//普通字段
                col.setComment(rs.getString("REMARKS"));
                col.setDecimalDigits(rs.getInt("decimal_digits"));
                
                if (needId == false) {
                    if ("ID".equals(col.getColumnName())) {
                        continue;
                    }
                }
                
                col.setJavaType(getJavaType(col));
                if (DATE_TYPE.equals(col.getJavaType())) {
                    ++dateColNum;
                }
                if (DECIMAL_TYPE.equals(col.getJavaType())) {
                    ++decimalColNum;
                }
                mapCols.put(col.getColumnName(), col);
                
                if (pks.contains(col.getColumnName())) {
                    col.setColType("PK");//主键
                    ++pkNum;
                }
                allCols.add(col);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (allCols.isEmpty()) {// 没查到字段
            System.err.println(lowerTableName + " 没有字段！");
            return map;
        }
        
        //处理分表的
        
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(lowerTableName);
        tableInfo.setClassName(getClassName(lowerTableName) + "Entity");
        String packageName = getPackageName(entityPath);
        tableInfo.setPackageName(packageName);
        tableInfo.setAllCols(allCols);
        tableInfo.setDateColNum(dateColNum);
        tableInfo.setDecimalColNum(decimalColNum);
         //实体类所在绝对路径
        String entityClassName = tableInfo.getPackageName() + "." + tableInfo.getClassName();
        tableInfo.setEntityUUID(String.valueOf(entityClassName.hashCode()));
        if (pkNum <= 0) {// 没有主键
            System.err.println(tableInfo.getTableName() + " 没有主键???");
        }
        if (shardNum <= 0) {// 没有分片键
            //System.err.println(tableInfo.getTableName() + " 没有分片键???");
        }
        
        // 保存到文件
        try {
            gen(tableInfo, tableInfo.getClassName(),entityPath);
            map.put("entityClassName",entityClassName);
            map.put("packageName",packageName);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    private static void gen(TableInfo tableInfo, String fileName,String entityPath) throws Exception {
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
       // cfg.setDirectoryForTemplateLoading(templateFile);
        cfg.setClassForTemplateLoading(JdbcTools.class,"/template");
        MapBean root = new MapBean();
        Template t = cfg.getTemplate("entity.templates");
        root.put("t", tableInfo);
        root.put("first2Lower", new First2Lower());
        root.put("first2Upper", new First2Upper());
        root.put("tabNameFormat", new TabNameFormat());
        t.process(root, new OutputStreamWriter(getOutput(fileName + ".java",entityPath ), "UTF-8"));
    }
    
    public static OutputStream getOutput(String fileName,String entityPath)
        throws IOException {
       // String outputDir = PropertiesUtil.getProperty("output.dir");
        if (StringHelper.isEmpty(entityPath)) {
           /* File testClassesPath = ResourceUtils.getFile("classpath:.");
            File targetPath = testClassesPath.getParentFile();
            File path = targetPath.getParentFile()*/;
            File dir = new File( entityPath);
            File saveFile = new File(dir, fileName);
            System.out.println("file save in : " + saveFile.getAbsolutePath());
            return new FileOutputStream(saveFile);
        }
        
        if (StringHelper.isEmpty(entityPath)) {
            return System.out;
        }
        File dir = null;
        URL url = Thread.currentThread().getContextClassLoader().getResource(entityPath);
        if (url != null) {
            dir = new File(url.getFile());
        } else {
            dir = new File(entityPath);
        }
        
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                return System.out;
            }
        }
        File saveFile = new File(dir, fileName);
        System.out.println("file save in : " + saveFile.getAbsolutePath());
        return new FileOutputStream(saveFile);
    }
    
    private static String getJavaType(ColumnInfo col) {
        String javaType = toJavaType(col);
        if (col.getDecimalDigits() > 0 && LONG_TYPE.equalsIgnoreCase(javaType)) {
            //有小数位
            javaType = DOUBLE_TYPE;
            col.setJavaType(javaType);
        }
        
        //处理默认值
        col.setDefValue("null");
        return javaType;
    }
    
    private static String toJavaType(ColumnInfo col) {
        String sqlType = col.getDataType();
        
        String dataType = col.getDataType();
        if (dataType.equalsIgnoreCase("NUMBER")) {
            if (col.getDecimalDigits() == 0) {
                return LONG_TYPE;
            } else {
                return DOUBLE_TYPE;
            }
        } else if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase("TIMESTAMP(6)")) {
            return DATE_TYPE;
        } else if ("BLOB".equalsIgnoreCase(dataType)) {
            return BINARY_TYPE;
        }
        
        //整数类型
        if (sqlType.equalsIgnoreCase("bit")) {//0 or 1
            return LONG_TYPE;//"Boolean";
        }
        if (sqlType.equalsIgnoreCase("tinyint")) {
            return LONG_TYPE;//return "Byte";
        }
        if (sqlType.equalsIgnoreCase("smallint")) {
            return INTEGER_TYPE;//return "Short";
        }
        if (sqlType.equalsIgnoreCase("int")) {
            return INTEGER_TYPE;//return "Integer";
        }
        if (sqlType.equalsIgnoreCase("bigint")) {
            return LONG_TYPE;
        }
        
        //浮点
        if (sqlType.equalsIgnoreCase("float")) {
            return FlOAT_TYPE;
        }
        if ((sqlType.equalsIgnoreCase("numeric")) || (sqlType.equalsIgnoreCase("real"))) {
            return DOUBLE_TYPE;
        }
        if ((sqlType.equalsIgnoreCase("money")) || (sqlType.equalsIgnoreCase("smallmoney"))) {
            return DOUBLE_TYPE;
        }
        //定点数
        if (sqlType.equalsIgnoreCase("decimal")) {
            return DECIMAL_TYPE;
        }
        
        //字符串
        if ("VARCHAR2".equalsIgnoreCase(sqlType)) {
            return STRING_TYPE;
        }
        if ((sqlType.equalsIgnoreCase("varchar")) || (sqlType.equalsIgnoreCase("char"))
            || (sqlType.equalsIgnoreCase("nvarchar")) || (sqlType.equalsIgnoreCase("nchar"))) {
            return STRING_TYPE;
        }
        if (sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("mediumtext")
            || sqlType.equalsIgnoreCase("longtext")) {
            return STRING_TYPE;
        }
        
        //日期类型
        if ((sqlType.equalsIgnoreCase("DATE")) || (sqlType.equalsIgnoreCase("year"))) {
            return DATE_TYPE;
        }
        if ((sqlType.equalsIgnoreCase("TIMESTAMP")) || (sqlType.equalsIgnoreCase("DATETIME"))) {
            return DATE_TYPE;
        }
        
        //Blob
        if (sqlType.equalsIgnoreCase("image")) { //Blob
            return BINARY_TYPE;
        }
        
        return STRING_TYPE;
    }
    
    public static String getClassName(String tableName) {
        if (tableName.startsWith("tb_") || tableName.startsWith("app_")) {
            tableName = tableName.substring(tableName.indexOf('_') + 1);
        }
        
        StringBuilder sb = new StringBuilder();
        String[] tabs = tableName.split("_");
        for (String s : tabs) {
            if (StringHelper.isEmpty(s)) {
                return s;
            }
            sb.append( Character.toUpperCase(s.charAt(0)) + s.substring(1));
        }
        return sb.toString();
    }
    
    private static String getPackageName(String entityPath) {
        String replace = null;
        if (StringHelper.isNotEmpty(entityPath)) {
           // String replace1 = entityPath.replace("\\", ".");
            String[] split = entityPath.split("java"+".");
            if(null != split && 1<split.length){
                  replace = split[1].replace("\\/", ".");
                return replace;
            }else
            {
               System.out.println("目录不符合规范，请配置目录为java的文件目录");
                System.exit(-1);
            }
        }

        return replace+" ";
    }

}
