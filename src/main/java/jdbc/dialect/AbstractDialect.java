package jdbc.dialect;

public abstract class AbstractDialect implements IDialect {
    
    public String buildCountSql(String sql) {
        StringBuilder pageSql = new StringBuilder();
        pageSql.append("SELECT count(1) as count FROM ( ").append(sql).append(") C$ WHERE 1=1 ");
        return pageSql.toString();
    }

	public String buildDateSql() {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildSequenceSql(String seqName, int num) {
		// TODO Auto-generated method stub
		return null;
	}
}
