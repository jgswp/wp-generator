package domain.entity;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import bean.Column;
import bean.ColumnType;
import bean.Table;
import pubUtils.BeanUtils;
import pubUtils.ReflectUtil;
import pubUtils.StringHelper;

/**
 * 表介质
 * 
 * @author wangpeng
 *
 */
public final class TableEntityMeta extends AbstractTableEntityMeta {
    private Class<?>                entityType;       // 绑定实体类型
    private String                  tableName;        // 表名
    private List<ColumnInfo>        columns;          // 所有字段
    private List<ColumnInfo>        conditionColumns; // 主键和分片键条件字段
    private StringBuilder           tableColumnNames; // 字段名(统一小写，多个逗号隔开)
    private StringBuilder           selectColumnAlias;// 字段as别名(统一小写，多个逗号隔开)
    private Map<String, ColumnInfo> mapColumns;       // 所有字段字段信息Map
    
    TableEntityMeta(Class<?> entityType) {
        Assert.notNull(entityType, "entity cannot be null");
        this.entityType = entityType;
        
        // 表信息
        Table table = getTable(entityType);
        Assert.notNull(table, "need annotation com.eshore.crmpub.domain.annotation.Table");
        String tableName = table.name();
        if (StringHelper.isEmpty(tableName)) {
            String entityName = entityType.getSimpleName();
            if (entityName.endsWith("Entity")) {
                entityName = entityName.substring(0, entityName.length() - 6);
            }
            tableName = StringHelper.propertyName2ColumnName(StringHelper.first2Lower(entityName));
        }
        this.tableName = tableName.toLowerCase();// 表名(统一小写)
        
        // 字段信息
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityType);
        Assert.notEmpty(pds, "entity's property empty");
        
        int index = 1;
        this.columns = new ArrayList<ColumnInfo>();
        this.tableColumnNames = new StringBuilder();
        this.selectColumnAlias = new StringBuilder();
        this.conditionColumns = new ArrayList<ColumnInfo>();
        this.mapColumns = new HashMap<String, ColumnInfo>(pds.length);
        for (PropertyDescriptor pd : pds) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                continue;// 没有getter和writer方法
            }
            
            Column column = readMethod.getAnnotation(Column.class);
            if (column == null) {
                continue;// 没注解为字段
            }
            
            // 保存字段信息
            String colName = column.name();
            if (StringHelper.isEmpty(colName)) {// 没注解名称
                colName = StringHelper.propertyName2ColumnName(pd.getName());
            }
            
            ColumnInfo colInfo = new ColumnInfo();
            colInfo.setPd(pd);
            colInfo.setColName(colName.toLowerCase());// 字段名(统一小写)
            colInfo.setClassType(pd.getPropertyType());// 字段java类型
            colInfo.setPropName(pd.getName());// 属性名
            colInfo.setIndex(index++);
            colInfo.setColType(column.type());
            addColumn(colInfo, null);
            this.mapColumns.put(colInfo.getPropName(), colInfo);
            this.mapColumns.put(colInfo.getPropName().toLowerCase(), colInfo);
        }
        Assert.notEmpty(columns, "need annotation com.eshore.crmpub.domain.Column");
        
        // 注册
        EntityManager.regEntityMeta(entityType, this);
    }
    
    private Table getTable(Class<?> entityType) {
        Table table = null;
        if (AbstractTableEntity.class.isAssignableFrom(entityType)) {
            Class<?> curType = entityType;
            while (true) {
                table = curType.getAnnotation(Table.class);
                if (table != null) {
                    break;
                }
                curType = curType.getSuperclass();
                if (curType == AbstractTableEntity.class) {
                    break;
                }
            }
        }
        return table;
    }
    
    private void addColumn(ColumnInfo colInfo, String valueStr) {
        // 字段名(统一小写，多个逗号隔开)
        if (colInfo.getIndex() > 1) {
            this.tableColumnNames.append(",");
            this.selectColumnAlias.append(",");
        }
        
        //移历史用
        this.tableColumnNames.append(colInfo.getColName());
        
        //查询用
        if (colInfo.getPropName().equalsIgnoreCase(colInfo.getColName())) {
            this.selectColumnAlias.append(colInfo.getPropName());
        } else {
            if (DB_KEYS.contains(colInfo.getPropName().toLowerCase())) {
                this.selectColumnAlias.append(colInfo.getColName()).append(" as \"")
                    .append(colInfo.getPropName()).append("\"");
            } else {
                this.selectColumnAlias.append(colInfo.getColName()).append(" as ")
                    .append(colInfo.getPropName());
            }
        }
        
        boolean isPk = colInfo.getColType() == ColumnType.PK;
        boolean isShard = colInfo.getColType() == ColumnType.SHARD;
        //boolean isShardUnused = colInfo.getColType() == ColumnType.SHARD_UNUSED;
        
        if (isPk || isShard) {
            if (isPk || isShard) {//主键or分片键
                conditionColumns.add(colInfo);
            }
        }
        
        columns.add(colInfo);
    }
    
    /**
     * 构造用于复制
     */
    private TableEntityMeta() {
    }
    
    /**
     * 复制
     * 
     * @return 表介质基类
     */
    private TableEntityMeta copy() {
        TableEntityMeta meta = new TableEntityMeta();
        meta.entityType = entityType;
        meta.tableName = tableName;
        meta.tableColumnNames = new StringBuilder(this.tableColumnNames);
        meta.selectColumnAlias = new StringBuilder(this.selectColumnAlias);
        meta.columns = new ArrayList<ColumnInfo>(this.columns);
        meta.conditionColumns = new ArrayList<ColumnInfo>(this.conditionColumns);
        return meta;
    }
    
    /**
     * 更换表（字段保留不变）
     * 
     * @param tableName
     *            表名
     * @return 更换表后的表介质
     */
    public TableEntityMeta switchTable(String tableName) {
        TableEntityMeta meta = copy();
        meta.tableName = tableName;
        return meta;
    }
    
    /**
     * 切换到历史表
     * 
     * @param hisTableName
     *            切换历史表
     * @param hisDateColName
     *            历史表记录创建时间字段名，如没有则可空
     * @return 更换表后的表介质
     */
    public TableEntityMeta switchHistTable(String hisTableName, String hisDateColName,
        String hisDateColValue) {
        TableEntityMeta mapper = switchTable(hisTableName);
        ColumnInfo colInfo = new ColumnInfo();
        colInfo.setColName(hisDateColName.toLowerCase());// 字段名(统一小写)
        colInfo.setColType(ColumnType.COL);
        colInfo.setClassType(Date.class);// 字段java类型
        colInfo.setPropName(StringHelper.columnName2propertyName(hisDateColName));// 属性名
        colInfo.setIndex(mapper.getColumns().size());
        mapper.addColumn(colInfo, hisDateColValue);
        return mapper;
    }
    
    /**
     * 获取在用库移到历史表的SQl语句
     * 
     * @param colNames
     *            历史表多了的字段
     * @param colValueStrs
     *            历史表多了字段的赋值
     * @return 历史表的SQl语句
     */
    public String getMoveHistSql(String hisTableName, String[] colNames, String[] colValueStrs) {
        StringBuilder moreColNames = new StringBuilder();
        StringBuilder moreColValues = new StringBuilder();
        if (colNames != null) {
            for (int i = 0; i < colNames.length; ++i) {
                moreColNames.append(colNames[i]).append(",");
                moreColValues.append(colValueStrs[i]).append(",");
            }
        }
        StringBuilder sql = new StringBuilder().append("insert into ").append(hisTableName)
            .append(" (");
        sql.append(moreColNames);
        sql.append(tableColumnNames).append(") select ");
        sql.append(moreColValues);
        sql.append(tableColumnNames).append(" from ").append(getTableName()).append(" ");
        return sql.toString();
    }
    
    /**
     * 获取查询语句(不带where条件)
     * 
     * @return 查询语句(不带where条件)
     */
    public String getSelectSql() {
        return new StringBuilder().append("select ").append(selectColumnAlias).append(" from ")
            .append(getTableName()).toString();
    }
    
    /**
     * 构造表类实体
     * 
     * @return 表类实体
     */
    @SuppressWarnings("unchecked")
    public <E> E newEntity() {
        try {
			return (E) ReflectUtil.newInstance(entityType);
		} catch (Exception e) {
			System.out.println("构造实例异常");			
			e.printStackTrace();
		}
		return null;
    }
    
    /**
     * 获取绑定的实体类型
     * 
     * @return 实体类型
     */
    public Class<?> getEntityType() {
        return this.entityType;
    }
    
    /**
     * 获取Entity绑定的表名
     * 
     * @return 表名
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * 获取所有字段信息
     * 
     * @return 所有字段信息
     */
    public List<ColumnInfo> getColumns() {
        return columns;
    }
    
    /**
     * 获取主键和分片健字段信息
     * 
     * @return 主键和分片健字段信息
     */
    public List<ColumnInfo> getConditionColumns() {
        return conditionColumns;
    }
    
    /** (non-Javadoc)
     * @see com.eshore.crmpub.domain.entity.AbstractTableEntityMeta#getColumnInfo(java.lang.String)
     */
    @Override
    public ColumnInfo getColumnInfo(String propName) {
        return mapColumns.get(propName.toLowerCase());
    }
    
}
