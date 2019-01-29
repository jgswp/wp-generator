package jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.util.Assert;

/**
 * 命名参数集,如：“where name = :name”
 * 
 * @author wangpeng
 *
 */
public class NamedParameters extends HashMap<String, Object> implements INamedParameters {
    private static final long serialVersionUID = -8849291968454663816L;
    
    /**
     * 创建一个空查询参数集
     * 
     * @return 一个基于Map的查询参数集
     */
    public static INamedParameters create() {
        return new NamedParameters();
    }
    
    NamedParameters() {
        super();
    }
    
    /**
     * 添加一个命名参数
     * 
     * @param key
     *            参数名称
     * @param value
     *            参数值
     * @return 当前对象本身
     */
    public NamedParameters add(String key, Object value) {
        this.put(key, value);
        return this;
    }
    
    @Override
    public NamedParameters put(String key, Object value) {
        Assert.hasLength(key, "NamedParameters参数key不能为空");
        super.put(key, value);
        return this;
    }
    
    /**
     * 将另一个NamedParameters合并进来。
     * 
     * @param other
     *            要合并的参数集
     * @return 该对象本身。其参数集是原有的参数集与另一个参数集合并后的结果
     */
    public INamedParameters add(INamedParameters other) {
        if (other != null) {
            super.putAll(other.getParams());
        }
        return this;
    }
    
    /**
     * 获得参数Map
     * 
     * @return 参数Map
     */
    @Override
    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(this);
    }
    
    /**
     * 获得对象的哈希值
     * 
     * @return 对象的哈希值
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 43).append(this).toHashCode();
    }
    
    /**
     * 判断参数集对象的等价性。当且仅当两个NamedParameters包含的参数Map相同时，两个对象才是等价的。
     * 
     * @param other
     *            另一个对象
     * @return 如果当前对象等价于other则返回true，否则返回false。
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NamedParameters)) {
            return false;
        }
        NamedParameters that = (NamedParameters) other;
        return new EqualsBuilder().append(this, that).isEquals();
    }
    
    @Override
    public String toString() {
        Iterator<Map.Entry<String, Object>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Map.Entry<String, Object> e = i.next();
            Object value = e.getValue();
            if (value != null) {
                sb.append("(").append(value.getClass().getSimpleName()).append(")");
            }
            sb.append(e.getKey());
            sb.append('=').append(value);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }
}
