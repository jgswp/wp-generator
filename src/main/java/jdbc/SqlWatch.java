package jdbc;

/**
 * SQL监视类
 * 
 * @author wangpeng
 */
public final class SqlWatch {
    private long    startTime;// 执行时间
    private long    stopTime; // 结束时间
    private boolean isError;  // 是否发生错误
    private String  errorMsg; // 错误信息
    
    /**
     * 构造
     */
    public SqlWatch() {
        this.startTime = -1;
        this.stopTime = -1;
        this.isError = false;
    }
    
    /**
     * 开始
     */
    public void start() {
        this.stopTime = -1L;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 停止
     */
    public void stop() {
        this.stopTime = System.currentTimeMillis();
    }
    
    /**
     * 获取SQL执行的消耗时间
     * 
     * @return 消耗时间（单位:毫秒）
     */
    public long getTime() {
        return this.stopTime - this.startTime;
    }
    
    /**
     * 是否发生错误
     * 
     * @return 如果发生错误返回true，否则返回false
     */
    public boolean isError() {
        return this.isError;
    }
    
    /**
     * 设置错误
     * 
     * @param errorMsg
     *            错误信息
     */
    public void setError(String errorMsg) {
        this.isError = true;
        this.errorMsg = errorMsg;
    }
    
    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    public String getErrorMsg() {
        return this.errorMsg;
    }
    
}
