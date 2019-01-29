package jdbc;

import pubUtils.StringHelper;

public class SqlDao {
    private final StringBuilder    sql;
    private final INamedParameters paramters;
    private String                 hint;     //查询hint 暂无
    
    public SqlDao() {
        this(null);
    }
    
    public SqlDao(String sql) {
        this(sql, NamedParameters.create());
    }
    
    public SqlDao(String sql, INamedParameters paramters) {
        this.sql = (StringHelper.isEmpty(sql) ? new StringBuilder()
            : new StringBuilder(sql));
        this.paramters = (paramters == null ? NamedParameters.create()
            : paramters);
    }
    
    public SqlDao clearSql() {
        this.sql.delete(0, this.sql.length());
        return this;
    }
    
    public SqlDao addSql(String sql) {
        this.sql.append(sql);
        return this;
    }
    
    public SqlDao cleanParam() {
        this.paramters.clear();
        return this;
    }
    
    public SqlDao addParam(String paramName, Object paramValue) {
        this.paramters.add(paramName, paramValue);
        return this;
    }
    
    public String getSql() {
        return sql.toString();
    }
    
    public INamedParameters getParamters() {
        return paramters;
    }
    
    public String getHint() {
        return hint;
    }
    
    public void setHint(String hint) {
        this.hint = hint;
    }
}
