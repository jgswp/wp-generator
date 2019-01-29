
package domain.entity;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import pubUtils.BeanUtils;
import pubUtils.ReflectUtil;

/**
 * @author wangpeng
 */
public class EntityMapper<E> extends AbstractRowMapper<E> {
    private final AbstractTableEntityMeta meta;
    private final Class<?>                clazz;
    
    public EntityMapper(AbstractTableEntityMeta meta, Class<?> clazz) {
        this.meta = meta;
        this.clazz = clazz;
    }
    
    @Override
    public String getColumnName(ResultSetMetaData rsmd, int index) throws SQLException {
        String columnName = super.getColumnName(rsmd, index);
        return columnName.toLowerCase();//统一小写
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E mapRow(ResultSet rs, int rowNum, String[] columnNames, int columnCount)
        throws SQLException {
        E mappedObject = null;
        if (clazz == null) {
            mappedObject = meta.newEntity();
        } else {
            mappedObject = (E) ReflectUtil.newInstance(clazz);
        }
        for (int i = 1; i <= columnCount; i++) {
            ColumnInfo colInfo = meta.getColumnInfo(columnNames[i - 1]);
            if (colInfo != null) {
                PropertyDescriptor pd = colInfo.getPd();
                if (pd != null) {
                    try {
                        Object value = getColumnValue(rs, i, pd);
                        if (clazz == null) {
                            BeanUtils.setPropertyValue(mappedObject, pd, value);
                        } else {
                            PropertyDescriptor dstPd = BeanUtils.getPropertyDescriptor(clazz,
                                pd.getName());
                            if (dstPd != null) {
                                BeanUtils.setPropertyValue(mappedObject, dstPd, value);
                            }
                        }
                    } catch (Throwable ignore) {
                    }
                }
            }
        }
        
        return mappedObject;
    }
    
}
