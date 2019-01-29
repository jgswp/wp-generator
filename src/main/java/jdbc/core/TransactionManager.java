package jdbc.core;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import jdbc.ITransaction;

/**
 * 数据库事务管理者
 * 
 * @author wangpeng
 */
public class TransactionManager extends DataSourceTransactionManager implements ITransaction {
    private static final long            serialVersionUID = 8255411883648948532L;
    private DefaultTransactionDefinition def              = null;                // 事务定义
    private TransactionStatus            status           = null;                // 事务状态
    
    /**
     * 构造
     * 
     * @param dataSource
     *            数据源
     * @param propagationBehavior
     *            事务级别定义（参考spring的TransactionDefinition类里的常量定义）
     */
    public TransactionManager(DataSource dataSource, int propagationBehavior) {
        super(dataSource);
        
        // 定义事务
        def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(propagationBehavior);
    }
    
    /**
     * 开始事务
     */
    public void start() {
        status = getTransaction(def);
    }
    
    /**
     * 提交事务
     */
    public void commit() {
        if (status != null) {
            commit(status);
            status = null;
        }
    }
    
    /**
     * 回滚事务
     */
    public void rollback() {
        if (status != null) {
            rollback(status);
            status = null;
        }
    }
    
}
