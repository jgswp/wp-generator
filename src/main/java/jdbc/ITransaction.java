package jdbc;

/**
 * 事务接口
 * @author wangpeng
 */
public interface ITransaction {
    /**
     * 开始事务
     */
    public void start();
    
    /**
     * 提交事务
     */
    public void commit();
    
    /**
     * 回滚事务
     */
    public void rollback();
    
}
