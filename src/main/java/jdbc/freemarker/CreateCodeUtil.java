package jdbc.freemarker;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.*;

import bean.MapBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.util.ResourceUtils;
import pubUtils.JdbcTools;
import pubUtils.PropertiesUtil;
import pubUtils.StringHelper;
import pubUtils.template.First2Lower;
import pubUtils.template.First2Upper;
import pubUtils.template.TabNameFormat;;


public class CreateCodeUtil {

    /**
     * 生成代码
     */
    public static void proCode(String s,String table,String nameSpace
            ,String url,String username,String password,String driverClassName,String  mapperXmlPath) throws Exception{
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        cfg.setClassForTemplateLoading(JdbcTools.class,"/template");
        Template t = cfg.getTemplate("mapperMysqlTemplate.ftl");
        //命名空间
        String pack = s;
        ReflectBean re = new ReflectBean(url,username,password,driverClassName);
        re.setTable(table);
        List<BeanModel> beanModels = re.doAction();
        Map<String,Object> root = new HashMap<String,Object>();
        String id = "";
       /* if(list.size()>=1){//创建数据模型
            id = list.get(list.size()-1);
        }*/
        String s2 = s.split("\\.")[1];
        root.put("nameSpace", nameSpace);
        root.put("pack", pack);	//包名
        root.put("objectName",s2 );							//类名
        root.put("objectNameLower", s.toLowerCase());		//类名(全小写)
        root.put("objectNameUpper", s.toUpperCase());		//类名(全大写)
        root.put("nowDate", new Date());							//当前日期
        root.put("table", table);
        root.put("id", "id");
        root.put("idbeanModel", beanModels.get(0));
        beanModels.remove( 0);
        root.put("fieldList", beanModels);

     /*   String s1 = getsoucePath();
        String path = getPath();*/
        /*生成mybatis xml*/
       // Freemarker.printFile("mapperMysqlTemplate.ftl", root, ""+ s2+"Mapper.xml", path, s1);

        t.process(root, new OutputStreamWriter(JdbcTools.getOutput(s2 + "Mapper.xml",mapperXmlPath), "UTF-8"));
    }


    /**
     * 生成代码
     */
    public static String proCodeMapper(String pack,String beanName ,String table,
             String url,String username,String password,String driverClassName,String mapperPath) throws Exception{

        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        // cfg.setDirectoryForTemplateLoading(templateFile);
        cfg.setClassForTemplateLoading(JdbcTools.class,"/template");

        Template t = cfg.getTemplate("mapper.ftl");
        ReflectBean re = new ReflectBean(url,username,password,driverClassName);
        re.setTable(table);
        List<BeanModel> beanModels = re.doAction();
        Map<String,Object> root = new HashMap<String,Object>();
        root.put("pack", pack);	//包名
        if(beanName.contains(".")) {
            String[] split = beanName.split("\\.");
            beanName =  split[split.length-1];
        }
        root.put("table",table);
        root.put("objectName", beanName);
        root.put("idbeanModel", beanModels.get(0));
        beanModels.remove( 0);
        root.put("fieldList", beanModels);
        //String s = getsoucePath();
       // System.out.println(s);
       // String path = getPath();
        /*生成Mapperl*/
       // System.out.println(path);
       // Freemarker.printFile("mapper.ftl", root, ""+ beanName+"Mapper.java", path, s);
        t.process(root, new OutputStreamWriter(JdbcTools.getOutput(beanName + "Mapper.java",  mapperPath), "UTF-8"));
         return pack+"."+ beanName+"Mapper";

    }

    private static String getPath( )  throws IOException {
        String outputDir = PropertiesUtil.getProperty("output.dir");
        if (StringHelper.isEmpty(outputDir)) {
            File testClassesPath = ResourceUtils.getFile("classpath:.");
            File targetPath = testClassesPath.getParentFile();
            File path = targetPath.getParentFile();
            String s =path + "\\src\\main\\java\\entity\\";

            return s;
        }


        URL url = Thread.currentThread().getContextClassLoader().getResource(outputDir);

        return   url.getPath();
    }

    private static String getsoucePath()  throws IOException {
            File testClassesPath = ResourceUtils.getFile("classpath:.");
            File targetPath = testClassesPath.getParentFile();
            File path = targetPath.getParentFile();
            return path.getPath()+"\\src\\main\\java\\jdbc\\freemarker\\";



    }
}
