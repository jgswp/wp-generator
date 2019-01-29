package bean;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author cdq
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {
    /**
     * 表名
     * @return
     */
    String name() default "";
}
