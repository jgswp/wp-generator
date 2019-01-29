package pubUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
	  
    private static Properties props;  
  
    static {  
        String fileName = "jdbctools.properties";
        props = new Properties();  
        try {  
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));  
        } catch (IOException e) { 
        	System.out.println("读取异常");
        }  
    }  
  
    public static String getProperty(String key){  
        String value = props.getProperty(key.trim());  
        if(StringHelper.isBlank(value)){  
            return null;  
        }  
        return value.trim();  
    }  
  
    public static String getProperty(String key,String defaultValue){  
  
        String value = props.getProperty(key.trim());  
        if(StringHelper.isBlank(value)){  
            value = defaultValue;  
        }  
        return value.trim();  
    }  
}
