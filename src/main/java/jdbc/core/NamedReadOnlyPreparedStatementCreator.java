package jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCreatorUtils;

/**
 * @author wangpeng
 */
public class NamedReadOnlyPreparedStatementCreator
    implements org.springframework.jdbc.core.PreparedStatementCreator, SqlProvider {
    private final String             sql;
    private final List<SqlParameter> declaredParameters;
    private final List<?>            parameters;
    
    public NamedReadOnlyPreparedStatementCreator(String sql, List<?> parameters) {
        this(sql, parameters, new LinkedList<SqlParameter>());
    }
    
    public NamedReadOnlyPreparedStatementCreator(String sql, List<?> parameters, int... types) {
        this(sql, parameters, SqlParameter.sqlTypesToAnonymousParameterList(types));
    }
    
    public NamedReadOnlyPreparedStatementCreator(String sql, List<?> parameters,
        List<SqlParameter> declaredParameters) {
        this.sql = sql;
        this.parameters = parameters;
        this.declaredParameters = declaredParameters;
        check();
    }
    
    private void check() {
        if (this.parameters.size() != declaredParameters.size()) {
            // account for named parameters being used multiple times
            Set<String> names = new HashSet<String>();
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof SqlParameterValue) {
                    names.add(((SqlParameterValue) param).getName());
                } else {
                    names.add("Parameter #" + i);
                }
            }
            if (names.size() != declaredParameters.size()) {
                throw new InvalidDataAccessApiUsageException("SQL [" + sql + "]: given "
                    + names.size() + " parameters but expected " + declaredParameters.size());
            }
        }
    }
    
    /** (non-Javadoc)
     * @see org.springframework.jdbc.core.PreparedStatementCreator#createPreparedStatement(java.sql.Connection)
     */
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(this.sql, ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
        ps.setFetchSize(Integer.MIN_VALUE);
        setValues(ps);
        return ps;
    }
    
    /** (non-Javadoc)
     * @see org.springframework.jdbc.core.SqlProvider#getSql()
     */
    public String getSql() {
        return this.sql;
    }
    
    private void setValues(PreparedStatement ps) throws SQLException {
        if (parameters == null) {
            return;
        }
        
        // Determine PreparedStatement to pass to custom types.
        PreparedStatement psToUse = ps;
        
        // Set arguments: Does nothing if there are no parameters.
        int sqlColIndx = 1;
        for (int i = 0; i < this.parameters.size(); i++) {
            Object in = this.parameters.get(i);
            SqlParameter declaredParameter;
            // SqlParameterValue overrides declared parameter metadata, in particular for
            // independence from the declared parameter position in case of named parameters.
            if (in instanceof SqlParameterValue) {
                SqlParameterValue paramValue = (SqlParameterValue) in;
                in = paramValue.getValue();
                declaredParameter = paramValue;
            } else {
                if (declaredParameters.size() <= i) {
                    throw new InvalidDataAccessApiUsageException(
                        "SQL [" + sql + "]: unable to access parameter number " + (i + 1)
                            + " given only " + declaredParameters.size() + " parameters");
                    
                }
                declaredParameter = declaredParameters.get(i);
            }
            if (in instanceof Collection && declaredParameter.getSqlType() != Types.ARRAY) {
                Collection<?> entries = (Collection<?>) in;
                for (Object entry : entries) {
                    if (entry instanceof Object[]) {
                        Object[] valueArray = ((Object[]) entry);
                        for (Object argValue : valueArray) {
                            StatementCreatorUtils.setParameterValue(psToUse, sqlColIndx++,
                                declaredParameter, argValue);
                        }
                    } else {
                        StatementCreatorUtils.setParameterValue(psToUse, sqlColIndx++,
                            declaredParameter, entry);
                    }
                }
            } else {
                StatementCreatorUtils.setParameterValue(psToUse, sqlColIndx++, declaredParameter,
                    in);
            }
        }
    }
    
}
