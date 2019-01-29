package jdbc;

import java.util.List;

/**
 * 命名参数集,如：“where name = :1 and name1=:2”
 * 
 * @author wangpeng
 *
 */
public interface IParameters extends List<Object> {
    /**
     * 添加一个命名参数
     * 
     * @param key
     *            参数名称
     * @param values
     *            参数值
     * @return 当前对象本身
     */
    public IParameters add(Object... values);

}
