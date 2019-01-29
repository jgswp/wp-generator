package pubUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextUtil implements ApplicationContextAware {
    protected static ApplicationContext applicationContext;// Spring应用上下文环境
    
    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     * 
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }
    
    public static ApplicationContext getApplicationContext() {
        return SpringContextUtil.applicationContext;
    }
    
    public static <T> T getBean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }
    
    public static Object getBean(String beanName) {
        if (applicationContext == null) {
            return null;
        }
        
        try {
            Object obj = applicationContext.getBean(beanName);
            if (obj != null) {
                return obj;
            }
        } catch (Throwable ignore) {
        }
        
        try {
            ClassLoader clsLoader = applicationContext.getClassLoader();
            Class<?> cls = clsLoader.loadClass(beanName);
            Object obj = applicationContext.getBean(cls);
            if (obj != null) {
                return obj;
            }
        } catch (Throwable ignore) {
        }
        
        return null;
    }
    
}
