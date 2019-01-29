package domain.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pubUtils.StringHelper;

public final class EntityManager {
    private static Map<Object, AbstractTableEntityMeta> mapEntityMetas = new ConcurrentHashMap<Object, AbstractTableEntityMeta>();
    private static Map<String, String>                  columnSqlMap   = new ConcurrentHashMap<String, String>();
    
    public static AbstractTableEntityMeta getEntityMeta(Class<?> entityClass) {
        AbstractTableEntityMeta meta = mapEntityMetas.get(entityClass);
        if (meta != null) {
            return meta;
        }
        if (AbstractTableEntity.class.isAssignableFrom(entityClass)) {
            meta = new TableEntityMeta(entityClass);
            regEntityMeta(entityClass, meta);
        }
        return meta;
    }
    
    public static AbstractTableEntityMeta getEntityMeta(String tableName) {
        String tabName = tableName.toLowerCase();// 表名(统一小写)
        AbstractTableEntityMeta meta = mapEntityMetas.get(tabName);
        if (meta != null) {
            return meta;
        }
        return meta;
    }
    
    static void regEntityMeta(Class<?> entityClass, AbstractTableEntityMeta meta) {
        mapEntityMetas.put(entityClass, meta);
        mapEntityMetas.put(meta.getTableName(), meta);
    }
    
    static void clear() {
        mapEntityMetas.clear();
    }
    
    /**
     * 获取单表查询字段的Sql片段
     * @param entityClazz 实体类
     * @param alias 表别名
     * @return Sql片段
     */
    public static String getColumnSql(Class<?> entityClass, String alias) {
        return getColumnSql(entityClass, alias, null);
    }
    
    /**
     * 获取单表查询字段的Sql片段
     *
     * @param entityClass 实体类
     * @param alias       表别名
     * @param prefix      前缀,默认null
     * @return Sql片段
     */
    public static String getColumnSql(Class<?> entityClass, String alias, String prefix) {
        if (prefix != null) {
            prefix = prefix.trim();
        } else {
            prefix = StringHelper.EMPTY;
        }
        
        StringBuilder keyStr = new StringBuilder(entityClass.getName()).append("@").append(alias);
        if (StringHelper.isNotEmpty(prefix)) {
            keyStr.append("#").append(prefix).toString();
        }
        
        String key = keyStr.toString();
        String colSql = columnSqlMap.get(key);
        if (colSql != null) {
            return colSql;
        }
        
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(entityClass);
        List<ColumnInfo> columns = meta.getColumns();
        StringBuilder sql = new StringBuilder();
        for (ColumnInfo column : columns) {
            if (sql.length() > 0) {
                sql.append(',');
            }
            sql.append(alias).append('.').append(column.getColName()).append(" as ").append(prefix)
                .append(column.getPropName());
        }
        colSql = sql.toString();
        columnSqlMap.put(key, colSql);
        return colSql;
    }
    
}
