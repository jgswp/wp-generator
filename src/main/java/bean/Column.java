package bean;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author cdq
 */
@Target({METHOD, FIELD })
@Retention(RUNTIME)
public @interface Column {
    /**
     * 字段名
     */
    String name() default "";
    
    /**
     * 字段类型
     * @return
     */
    ColumnType type() default ColumnType.COL;
}
