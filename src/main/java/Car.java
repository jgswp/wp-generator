import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import pubUtils.JdbcTools;
/**
 *
 *   print
 */
@Mojo(name = "print")
public class Car extends AbstractMojo {
    @Parameter
    private String mapperJavaPath;
    @Parameter
    private String mapperXmlPath;
    @Parameter
    private String entityPath;
    @Parameter
    private String tables;
    @Parameter
    private String dbDriver;
    @Parameter
    private String url;
    @Parameter
    private String user;
    @Parameter
    private String password;
      public void execute() throws MojoExecutionException, MojoFailureException {

          getLog().info("===== 生成代码开始 =====");
          System.out.println( tables+ " "+ dbDriver+ " "+url+ " "+user+ " "+password+ " "+"root"+ " "+mapperJavaPath+ " "+mapperXmlPath+ " "+entityPath);
          if(StringUtils.isBlank(mapperJavaPath)||StringUtils.isBlank(mapperXmlPath)|| StringUtils.isBlank(entityPath)){
              getLog().error("请配置文件生成目录");
          }else{
              getLog().info("===== 生成mapperXmlPath ===== "+mapperXmlPath);
              System.out.println( tables+ " "+ dbDriver+ " "+url+ " "+user+ " "+password+ " "+"root"+ " "+mapperJavaPath+ " "+mapperXmlPath+ " "+entityPath);
              JdbcTools.create(  tables,   dbDriver,   url,   user,   password,   "root",   mapperJavaPath,   mapperXmlPath,   entityPath);
          }

      }


}