package domain.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractTableEntityMeta extends AbstractEntityMeta {
    protected final static Set<String> DB_KEYS;//数据库用到的一些关键字
    
    static {
        //mysql关键字(统一小写)
        Set<String> dbKeys = new HashSet<String>();
        dbKeys.add("maxvalue");
        DB_KEYS = dbKeys;
    }
    
    /**
     * 获取绑定的实体类型
     * 
     * @return 实体类型
     */
    public abstract Class<?> getEntityType();
    
    /**
     * 构造表类实体
     * 
     * @return 表类实体
     */
    public abstract <E> E newEntity();
    
    /**
     * 获取Entity绑定的表名
     * 
     * @return 表名
     */
    public abstract String getTableName();
    
    /**
     * 获取所有字段信息
     * 
     * @return 所有字段信息
     */
    public abstract List<ColumnInfo> getColumns();
    
    /**
     * 获取主键和分片的字段信息
     * 
     * @return 所有字段信息
     */
    public abstract List<ColumnInfo> getConditionColumns();
    
    /**
     * 字段信息
     * 
     * @param propName
     *            属性名
     * @return 字段信息
     */
    public abstract ColumnInfo getColumnInfo(String propName);
    
    /**
     * 获取查询语句(不带where条件)
     * 
     * @return 查询语句(不带where条件)
     */
    public abstract String getSelectSql();
    
    /**
     * 更换表（字段保留不变）
     * 
     * @param tableName
     *            表名
     * @return 更换表后的表介质
     */
    public abstract AbstractTableEntityMeta switchTable(String tableName);
    
    /**
     * 切换到历史表
     * 
     * @param hisTableName
     *            切换历史表
     * @param hisDateColName
     *            历史表记录创建时间字段名，如没有则可空
     * @param hisDateColValue
     *            历史表记录创建时间字段的取值
     * @return 更换表后的表介质
     */
    public abstract AbstractTableEntityMeta switchHistTable(String hisTableName,
        String hisDateColName, String hisDateColValue);
    
    /**
     * 获取移到历史表的SQl语句
     * 
     * @param colNames
     *            历史表多了的字段
     * @param colValueStrs
     *            历史表多了字段的赋值
     * @return 历史表的SQl语句
     */
    public abstract String getMoveHistSql(String hisTableName, String[] colNames,
        String[] colValueStrs);
    
}
