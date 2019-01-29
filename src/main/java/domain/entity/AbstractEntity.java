package domain.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 抽象实体类，可作为所有实体的基类
 * 
 * @author wangpeng
 *
 */
public abstract class AbstractEntity implements IEntity {
    private static final long serialVersionUID = 1829164529801604952L;
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, YDToStringStyle.DEFAULT_STYLE);
    }
}
