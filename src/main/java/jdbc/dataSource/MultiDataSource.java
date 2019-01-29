package jdbc.dataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import bean.YDException;
import pubUtils.StringHelper;

/**
 * 多数据库连接的数据源
 * 
 * @author cdq
 * 
 */
public class MultiDataSource implements DataSource, BeanFactoryAware {
    private final static int     WARNTIME    = 30;                                      //获取连接时间超长写日志的伐值
    private  static Logger       dbLogger    = Logger.getLogger("YD_dbSource");
    private BeanFactory          beanFactory = null;                                    // spring的Bean工厂类
    private final String         dataSourceId = "YD_dbSource";							//本地数据源beanId
    /**
     * 获取数据库连接
     * 
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        long st = System.currentTimeMillis();
        try {
            return this.getDataSource().getConnection();
        } finally {
            long cst = System.currentTimeMillis() - st;
            if (cst > WARNTIME) {
                dbLogger.info(" get connection from datasource " + cst + " ms");
            }
        }
    }
    
    /**
     * 获取数据库连接
     * 
     * @see javax.sql.DataSource#getConnection(java.lang.String,
     *      java.lang.String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        long st = System.currentTimeMillis();
        try {
            return this.getDataSource().getConnection(username, password);
        } finally {
            long cst = System.currentTimeMillis() - st;
            if (cst > WARNTIME) {
                dbLogger.info(" get connection from datasource " + cst + " ms");
            }
        }
    }
    
    /**
     * 根据数据源的唯一标识获取数据源
     * 
     * @param dataSourceId
     *            数据源的唯一标识
     * @return 数据源
     */
    protected DataSource getDataSource(String dataSourceId) {
        DataSource dataSource = null;
        if (beanFactory.containsBean(dataSourceId)) {
            Object dataSourceObj = beanFactory.getBean(dataSourceId);
            if (dataSourceObj != null && dataSourceObj instanceof DataSource) {
                dataSource = (DataSource) dataSourceObj;
               /* dbLogger.info(
                    new StringBuilder("find a dataSource from spring beanFactory, dataSourceId:")
                        .append(dataSourceId).toString());*/
            } else {
                dbLogger.warning(new StringBuilder("dataSource[").append(dataSourceId)
                    .append("] is not a real javax.sql.DataSource class in spring beanFactory")
                    .toString());
            }
        } else {
            dbLogger.warning("no beanId=" + dataSourceId + " in spring beanFactory");
        }
        return dataSource;
    }
    
    /**
     * 获取数据源
     * 
     * @return 数据源
     */
    public DataSource getDataSource() {
        DataSource dataSource = null;
        try {
            if (StringHelper.isNotEmpty(dataSourceId)) {
                //dbLogger.info("we use datasource=" + dataSourceId);
                dataSource = this.getDataSource(dataSourceId);
                if (dataSource == null) {
                    StringBuilder errMsg = new StringBuilder("can't get datasource for id=")
                        .append(dataSourceId);
                    dbLogger.warning(errMsg.toString());
                }
            } else {
                dbLogger.warning("datasourceId=null is invalid.");
            }
        } catch (Throwable e) {
            dbLogger.warning("获取数据库连接错误!dataSourceId=" + dataSourceId+e);
        }
        if (dataSource == null) {
            throw new YDException("获取数据库连接失败!dataSourceId=" + dataSourceId);
        }
        return dataSource;
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        return this.getDataSource().getLogWriter();
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        return this.getDataSource().getLoginTimeout();
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter printwriter) throws SQLException {
        this.getDataSource().setLogWriter(printwriter);
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int i) throws SQLException {
        this.getDataSource().setLoginTimeout(i);
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    
    /**
     * 关闭数据源
     */
    public void close() {
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return java.util.logging.Logger.getLogger("YD_dbSource");
    }
}
