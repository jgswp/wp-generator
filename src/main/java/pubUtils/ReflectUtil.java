package pubUtils;

import org.springframework.util.ClassUtils;

import bean.YDException;


/**
 * 反射工具类
 * 
 * @author cdq
 */
public class ReflectUtil {
    /**
     * 实例化类对象
     * 
     * @param className
     *            类名
     * @return 实例对象
     * @throws Exception 
     */
    public static final Object newInstance(String className) throws Exception {
        return newInstance(findClass(className));
    }
    
    /**
     * 实例化类对象
     * 
     * @param aClass
     *            java类
     * @return 实例对象
     * @throws Exception 
     */
    public static final Object newInstance(Class<?> aClass) {
        try {
            return aClass.newInstance();
        } catch (InstantiationException e) {
            // 实例化异常
            throw new YDException("className"+
                aClass.getName());
        } catch (IllegalAccessException e) {
            // 实例化异常
            throw new YDException("className"+
                aClass.getName());
        }
    }
    
    /**
     * 根据类名（string）找java类
     * 
     * @param className
     *            类名（string）
     * @return java类
     * @throws Exception 
     */
    public static final Class<?> findClass(String className) throws Exception {
        try {
            return ClassUtils.forName(className.trim(),
                Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new Exception("className"+
                className);
        }
    }
    
}
