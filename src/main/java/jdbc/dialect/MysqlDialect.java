package jdbc.dialect;

public class MysqlDialect extends AbstractDialect {
    private final static String   DATE_SQL = "SELECT NOW()";
    private final static IDialect instance = new MysqlDialect();
    
    public static IDialect getInstance() {
        return instance;
    }
    
    @Override
    public String buildDateSql() {
        return DATE_SQL;
    }
    
    @Override
    public String buildSequenceSql(String seqName, int num) {
        if (num < 1) {
            num = 1;
        }
        StringBuilder sql = new StringBuilder("select ").append(seqName).append(".nextval");
        if (num > 1) {
            sql.append("(").append(num).append(")");
        }
        return sql.toString();
    }
    
}
