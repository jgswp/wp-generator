package jdbc.dialect;

public interface IDialect {
    /**
     * 构造查询时间日期的SQL
     * 
     * @return 查询时间日期的SQL
     */
    public String buildDateSql();
    
    /**
     * 构造查询序列的SQL
     * 
     * @param seqName
     *            序列
     * @param num
     *            数量
     * @return SQL
     */
    public String buildSequenceSql(String seqName, int num);
    
    /**
     * 构造select count语句
     * 
     * @param sql
     *            SQL
     * @return select count语句
     */
    public String buildCountSql(String sql);
}
