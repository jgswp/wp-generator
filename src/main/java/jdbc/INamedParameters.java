package jdbc;

import java.util.Map;

/**
 * 命名参数集,如：“where name = :name”
 * 
 * @author wangpeng
 *
 */
public interface INamedParameters extends Map<String, Object> {
    /**
     * 获得参数Map
     * 
     * @return 参数Map
     */
    public Map<String, Object> getParams();
    
    /**
     * 添加一个命名参数
     * 
     * @param key
     *            参数名称
     * @param value
     *            参数值
     * @return 当前对象本身
     */
    public INamedParameters add(String key, Object value);
    
    /**
     * 将另一个INamedParameters合并进来。
     * 
     * @param other
     *            要合并的参数集
     * @return 该对象本身。其参数集是原有的参数集与另一个参数集合并后的结果
     */
    public INamedParameters add(INamedParameters other);
}
