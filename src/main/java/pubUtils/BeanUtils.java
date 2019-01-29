package pubUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ClassUtils;

import bean.MapBean;

/**
 * BeanUtils工具类
 * 
 * @author cdq
 */
public final class BeanUtils {
    
    public static MapBean toMap(Object objBean, Class<?> ignoreSuperCls) {
        MapBean map = new MapBean();
        obj2Map(map, objBean, ignoreSuperCls);
        return map;
    }
    
    /**
     * 将Object对象的属性转到Map容器里
     * 
     * @param map
     *            Map容器
     * @param objBean
     *            Object对象
     * @param ignoreSuperCls
     *            忽略超类为ignoreSuperCls属性
     */
    public static void obj2Map(MapBean map, Object objBean, Class<?> ignoreSuperCls) {
        if (objBean == null) {
            return;
        }
        
        Class<?> cls = objBean.getClass();
        if (ignoreSuperCls != null) {
            cls = findSuberClass(cls, ignoreSuperCls);
        }
        
        PropertyDescriptor pds[] = BeanUtils.getPropertyDescriptors(cls);
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            if ("class".equals(pd.getName())) {
                continue;
            }
            
            try {
                Method readMethod = pd.getReadMethod();
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                
                Object value = readMethod.invoke(objBean, new Object[0]);
                if (value == null) {
                    value = StringHelper.EMPTY;
                }
                
                map.put(pd.getName(), value);
            } catch (Throwable ex) {
                throw new RuntimeException("Could not copy properties from source to target", ex);
            }
        }
    }
    
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        return org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);
    }
    
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
        return org.springframework.beans.BeanUtils.getPropertyDescriptor(clazz, propertyName);
    }
    
    /**
     * 找到sourceClass直接继承于superClass的类
     * 
     * @param sourceClass
     *            当前类
     * @param superClass
     *            超类
     * @return 直接继承于superClass的类
     */
    private static Class<?> findSuberClass(Class<?> sourceClass, Class<?> superClass) {
        Class<?> tmpClass = sourceClass;
        while (tmpClass != Object.class && tmpClass.getSuperclass() != superClass) {
            tmpClass = tmpClass.getSuperclass();
        }
        return tmpClass;
    }
    
    /**
     * Bean属性装载到Map容器
     * 
     * @param javaBean
     *            javaBean对象
     * @return MapBean对象
     */
    public static MapBean toMap(Object javaBean) {
        MapBean mapBean = new MapBean();
        if (javaBean == null) {
            return mapBean;
        }
        
        Class<?> cls = javaBean.getClass();
        PropertyDescriptor pds[] = BeanUtils.getPropertyDescriptors(cls);
        for (int i = 0; i < pds.length; ++i) {
            PropertyDescriptor pd = pds[i];
            if ("class".equals(pd.getName())) {
                continue;
            }
            
            try {
                Method readMethod = pd.getReadMethod();
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                
                Object value = readMethod.invoke(javaBean, new Object[0]);
                if (value == null) {
                    continue;
                }
                
                mapBean.put(pd.getName(), value);
            } catch (Throwable e) {
                throw new RuntimeException("Could not copy properties from source to target", e);
            }
        }
        return mapBean;
    }
    
    /**
     * Bean属性装载到Map容器
     * 
     * @param javaBean
     *            javaBean对象
     * @return javaBean对象
     */
    public static <T> T fromMap(T javaBean, Map<?, ?> mapBean) {
        Class<?> cls = javaBean.getClass();
        PropertyDescriptor targetPds[] = org.springframework.beans.BeanUtils
            .getPropertyDescriptors(cls);
        for (int i = 0; i < targetPds.length; ++i) {
            PropertyDescriptor targetPd = targetPds[i];
            if ("class".equals(targetPd.getName())) {
                continue;
            }
            if (targetPd.getWriteMethod() == null) {
                continue;
            }
            
            Object value = mapBean.get(targetPd.getName());
            if (value == null) {
                continue;
            }
            
            try {
                Method writeMethod = targetPd.getWriteMethod();
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                writeMethod.invoke(javaBean, new Object[] {value });
            } catch (Throwable e) {
                throw new RuntimeException("Could not copy properties from source to target", e);
            }
        }
        return javaBean;
    }
    
    /**
     * 复制Bean对象
     * @param source 原Bean对象
     * @param cls 目标Bean对象Class属性
     * @return 目标Bean对象
     * @throws Exception 
     */
    public static <T> T copyBean(Object source, Class<T> cls) throws Exception {
        if (source == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T target = (T) ReflectUtil.newInstance(cls);
        copyProperties(source, target);
        return target;
    }
    
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, false, null);
    }
    
    public static void copyProperties(Object source, Object target, boolean ignoreNull) {
        copyProperties(source, target, ignoreNull, null);
    }
    
    public static void copyProperties(Object source, Object target, boolean ignoreNull,
        Set<String> ignoreProperties) {
        if (source == null || target == null) {
            return;
        }
        
        PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());
        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod == null) {
                continue;//没有setter方法
            }
            if (ignoreProperties != null && ignoreProperties.contains(targetPd.getName())) {
                continue;//忽略
            }
            
            PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(),
                targetPd.getName());
            if (sourcePd == null) {
                continue;//没有此属性
            }
            
            Method readMethod = sourcePd.getReadMethod();
            if (readMethod == null) {
                continue;//没有getter
            }
            
            if (!ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                readMethod.getReturnType())) {
             
            	//
                continue;//不可赋值类型
            }
            
            try {
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                
                Object value = readMethod.invoke(source);//get
                if (value == null && ignoreNull) {
                    continue;//忽略null
                }
                
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                writeMethod.invoke(target, value);//set
            } catch (Throwable ignore) {
            }
        }
    }
    
    /**
     * 获取属性的值
     * 
     * @param javaBean
     *            javaBean对象
     * @param pd
     *            javaBean属性
     * @return javaBean属性值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPropertyValue(Object javaBean, PropertyDescriptor pd) {
        T value = null;
        try {
            Method readMethod = pd.getReadMethod();
            if (readMethod != null) {
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                value = (T) readMethod.invoke(javaBean);
                return value;
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("通过getter取字段值失败！", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("通过getter取字段值失败！", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("通过getter取字段值失败！", e);
        } catch (Throwable t) {
            throw new RuntimeException("通过getter取字段值失败！", t);
        }
        return value;
    }
    
    /**
     * 设置属性值
     * 
     * @param javaBean
     *            javaBean对象
     * @param pd
     *            javaBean属性
     */
    public static void setPropertyValue(Object javaBean, PropertyDescriptor pd, Object value) {
        try {
            if (value == null) {
                return;
            }
            
            // 通过setter保存
            Method writeMethod = pd.getWriteMethod();
            if (writeMethod == null) {
                return;
            }
            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
            }
            
            Class<?> cls = null;
            Class<?>[] clss = writeMethod.getParameterTypes();
            if (clss != null && clss.length == 1) {
                cls = clss[0];
            }
            
            if (cls == null) {
                return;
            }
            
            if (!cls.isPrimitive()) {
                writeMethod.invoke(javaBean, value);
            } else if (double.class == cls) {
                writeMethod.invoke(javaBean, ((Double) value).doubleValue());
            } else if (long.class == cls) {
                writeMethod.invoke(javaBean, ((Long) value).longValue());
            } else if (short.class == cls) {
                writeMethod.invoke(javaBean, ((Short) value).shortValue());
            } else if (int.class == cls) {
                writeMethod.invoke(javaBean, ((Integer) value).intValue());
            } else if (boolean.class == cls) {
                writeMethod.invoke(javaBean, ((Boolean) value).booleanValue());
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("通过setter保存字段值失败！", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("通过setter保存字段值失败！", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("通过setter保存字段值失败！", e);
        } catch (Throwable t) {
            throw new RuntimeException("通过setter保存字段值失败！", t);
        }
    }
}
