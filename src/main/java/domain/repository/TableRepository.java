package domain.repository;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.Assert;

import bean.*;
import domain.entity.AbstractTableEntity;
import domain.entity.AbstractTableEntityMeta;
import domain.entity.ColumnInfo;
import domain.entity.EntityManager;
import domain.entity.EntityMapper;
import jdbc.IJdbcDao;
import jdbc.IParameters;
import jdbc.IRowSet;
import jdbc.Parameters;
import jdbc.SqlDao;
import pubUtils.BeanUtils;
import pubUtils.ReflectUtil;

public abstract class TableRepository implements ITableRepository{
	private final static String ENTITY_NULL_MSG = "entity is required; it must not be null";
	private final static String META_NULL_MSG   = " has not been struck off the register";
	
	protected IJdbcDao jdbcDao;
	
	public void setJdbcDao(IJdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
	}

	@Override
	public <E extends AbstractTableEntity> int insert(E e) {
		Assert.notNull(e, ENTITY_NULL_MSG);
        e.check();
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
        Assert.notNull(meta, e.getClass().getSimpleName() + META_NULL_MSG);
        
        StringBuilder fields = new StringBuilder();
        StringBuilder vars = new StringBuilder();
        StringBuilder errmsg = new StringBuilder();

        IParameters params = Parameters.create();
        List<ColumnInfo> columns = meta.getColumns();
        for (ColumnInfo column : columns) {
            ColumnType colType = column.getColType();
            Object value = readParam(e, column.getPd());

            boolean isInsertField = false;
            if(ColumnType.PK == colType){
            	  isInsertField = true;
                  if (value == null) {
                      errmsg.append(" column PK " + column.getColName() + " cannot be null!\n");
                  }
            }else if(value != null){
                isInsertField = true;
            }
            
            if(isInsertField){
            	if(!params.isEmpty()){
            		fields.append(",");
            		vars.append(",");
            	}
            	fields.append(column.getColName());
            	vars.append("?");
            	params.add(value);
            }
		}
        
        if (params.isEmpty()) {
            errmsg.append("No field is assigned!\n");
        }
        
        String tableName = meta.getTableName();
        if (errmsg.length() > 0) {
            String msg = errmsg.toString();
            throw new YDException("error "+tableName + ": " + msg);
        }
		
        String insertSql = new StringBuffer("insert into ").append(tableName)
        		.append("(").append(fields).append(") values (").append(vars).append(")").toString();
		return jdbcDao.execSql(insertSql, params);
	}

	@Override
	public <E extends AbstractTableEntity> int update(E e) {
        Assert.notNull(e, ENTITY_NULL_MSG);
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
        Assert.notNull(meta, e.getClass().getSimpleName() + META_NULL_MSG);

        StringBuilder setfields = new StringBuilder();
        StringBuilder wherefields = new StringBuilder();
        IParameters setparams = Parameters.create();
        IParameters whereparams = Parameters.create();
        StringBuilder errmsg = new StringBuilder();

        List<ColumnInfo> columns = meta.getColumns();
        String tableName = meta.getTableName();
        for (ColumnInfo column : columns) {
        	 ColumnType colType = column.getColType();
             Object value = readParam(e, column.getPd());
             if(ColumnType.PK == colType){
            	 if(value == null){
            		 throw new YDException(tableName + " PK "+column.getColName() + " empty!");
            	 }
            	 if(!whereparams.isEmpty()){
            		 wherefields.append(" and ");
            	 }
            	 wherefields.append(column.getColName()).append("=?");
            	 whereparams.add(value);
             }if(value != null){
            	 if(!setparams.isEmpty()){
            		 setfields.append(",");
            	 }
            	 setfields.append(column.getColName()).append("=?");
            	 setparams.add(value);
             }
		}
        if (setparams.isEmpty()) {
            errmsg.append("No update field is assigned!\n");
        }
		
        if (errmsg.length() > 0) {
            String msg = errmsg.toString();
            throw new YDException(tableName + ": " + msg);
        }
        
        String updateSql = new StringBuffer("update ").append(tableName).append(" set ")
        		.append(setfields).append(" where ").append(wherefields).toString();
        setparams.addAll(whereparams);
        
		return jdbcDao.execSql(updateSql, setparams);
	}

	@Override
	public <E extends AbstractTableEntity> int deleteByPK(E e) {
        Assert.notNull(e, ENTITY_NULL_MSG);
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
        Assert.notNull(meta, e.getClass().getSimpleName() + META_NULL_MSG);

        IParameters params = Parameters.create();
        String sql = this.getPKConditionSql(e, meta, params);
		
        String tableName = meta.getTableName();
        
        StringBuffer deleteSql = new StringBuffer("delete from ").append(tableName)
        		.append(" where ").append(sql);
        
		return jdbcDao.execSql(deleteSql.toString(), params);
	}

	public <E extends AbstractTableEntity> int deleteByNoPK(E e){
		Assert.notNull(e, ENTITY_NULL_MSG);
		AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
		Assert.notNull(meta, e.getClass().getSimpleName() + META_NULL_MSG);

		IParameters params = Parameters.create();
		String sql = this.getNotNullConditionSql(e, meta, params);
		String tableName = meta.getTableName();
		StringBuffer deleteSql = new StringBuffer("delete from ").append(tableName)
				.append(" where ").append(sql);
		return jdbcDao.execSql(deleteSql.toString(), params);
	}
	
	@Override
	public <E extends AbstractTableEntity> E get(E e) {
		Assert.notNull(e, ENTITY_NULL_MSG);
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
        Assert.notNull(meta, e.getClass().getSimpleName() + META_NULL_MSG);

        IParameters params = Parameters.create();
        String sql = this.getPKConditionSql(e, meta, params);
        String deleteSql = new StringBuffer(meta.getSelectSql())
        		.append(" where ").append(sql).toString();
		return jdbcDao.queryFirstRow(deleteSql.toString(), params, new EntityMapper<E>(meta, null));
	}
	
	private Object readParam(Object obj, PropertyDescriptor pd) {
	        return BeanUtils.getPropertyValue(obj, pd);
	}
	
	
	private String getNotNullConditionSql(Object e, AbstractTableEntityMeta meta, IParameters params) {
		StringBuffer sql = new StringBuffer();

		List<ColumnInfo> columns = meta.getColumns();
		for (ColumnInfo column : columns) {
			Object value = this.readParam(e, column.getPd());
			if(value != null){
				if(sql.length()>0){
					sql.append(" and ");
				}
				sql.append(column.getColName()).append("=?");
                params.add(value);
			}
		}
		if(params.isEmpty()){
			throw new YDException(meta.getTableName() +" sql 参数为空!");
		}
		return sql.toString();
	}
	
	private String getPKConditionSql(Object e, AbstractTableEntityMeta meta, IParameters params) {
		StringBuffer sql = new StringBuffer();
        StringBuilder errmsg = new StringBuilder();

		List<ColumnInfo> columns = meta.getColumns();
		for (ColumnInfo column : columns) {
			ColumnType colType = column.getColType();
			Object value = this.readParam(e, column.getPd());
			if(ColumnType.PK == colType && value == null){
				errmsg.append(" PK ").append(column.getColName()).append(" empty!");
				throw new YDException(meta.getTableName()+errmsg.toString());
			}
			
			if(value != null){
				if(sql.length()>0){
					sql.append(" and ");
				}
				sql.append(column.getColName()).append("=?");
                params.add(value);
			}
		}
		
		if(params.isEmpty()){
			throw new YDException(meta.getTableName() +" sql 参数为空!");
		}
		return sql.toString();
	}

    @Override
    public <E extends AbstractTableEntity> List<E> getList(E e){
    	Assert.notNull(e, ENTITY_NULL_MSG);
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
        Assert.notNull(meta, e.getClass().getSimpleName() + META_NULL_MSG);

        IParameters params = Parameters.create();
        String sql = this.getNotNullConditionSql(e, meta, params);
        String deleteSql = new StringBuffer(meta.getSelectSql())
        		.append(" where ").append(sql).toString();
		return jdbcDao.query(deleteSql.toString(), params, new EntityMapper<E>(meta, null));
    }
    
	 /**
     * 查询序列
     * 
     * @param seqName 序列
     * @return 一个序列值
     */
    public long getSeqID() {
        return jdbcDao.getSeqID();
    }
    
    /**
     * 获取数据库时间
     * 
     * @return 数据库时间
     */
    public Date getSysDate() {
        return jdbcDao.getSysDate();
    }
    
    @Override
    public <E extends AbstractTableEntity> List<E> getList(String sql, IParameters params,E e){
    	AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
		String sqlQuery = new StringBuffer(meta.getSelectSql()).append(sql).toString();
    	IRowSet rs = jdbcDao.query(sqlQuery, params);
    	List<E> list = new ArrayList<E>();
    	while(rs.next()){
    		@SuppressWarnings("unchecked")
			E es = (E) ReflectUtil.newInstance(e.getClass());
    		rs.saveToBean(es, null, true);
    		list.add(es);
    	}
		return list;
    }
    
    public <E extends AbstractTableEntity> List<E> getList(Class<E> tab, Map<String, Object> params){
    	AbstractTableEntityMeta meta = EntityManager.getEntityMeta(tab);
         SqlDao sqlParam = new SqlDao();
         sqlParam.addSql(" where 1 = 1 ");
         for (Entry<String, Object> e : params.entrySet()) {
             String key = e.getKey();
             Object obj = e.getValue();
             if(key.startsWith("or")|| key.startsWith(" or")){
            	 sqlParam.addSql(" " +key + "=" + "'" + obj + "'");
            	 continue;
             }
             
             if (obj instanceof String) {
                 if (obj.toString().indexOf(",") < 0 && !key.equals("limit")) {
                     sqlParam.addSql(" and ").addSql(key + "=" + "'" + obj + "'");
                 } else if(obj.toString().indexOf(",") > 0 && !key.equals("limit")){
                     sqlParam.addSql(" and ").addSql(key + " in(" + obj + ") ");
                 }
             } else if (obj instanceof Set) {
                 @SuppressWarnings("unchecked")
                 Set<Object> sets = (Set<Object>) obj;
                 sqlParam.addSql(" and ").addSql(key + " in(");
                 Iterator<Object> it = sets.iterator();
                 int i = 1;
                 while (it.hasNext()) {
                     Object objId = it.next();
                     sqlParam.addParam(i + "", objId);
                     if (i == sets.size()) {
                         sqlParam.addSql(":" + i);
                     } else {
                         sqlParam.addSql(":" + i + ",");
                     }
                     i++;
                 }
                 sqlParam.addSql(")");
             }
         }
         
         if(params.containsKey("limit")){
        	 sqlParam.addSql(" limit "+params.get("limit"));
         }
         return select(meta, sqlParam, null);
    }
    
    private  <T> List<T> select(AbstractTableEntityMeta meta, SqlDao sql,
    		Class<?> clazz) {
    	Assert.notNull(meta, "entity has not been struck off the register");
    	Assert.notNull(sql, "criterion is required; it must not be null");
    	Assert.hasLength(sql.getSql(), "sql criterion is required; it must not be null");
            
    	StringBuilder sqlUse = new StringBuilder(meta.getSelectSql()).append(" ")
    			.append(sql.getSql());
    	SqlDao sqlDao = new SqlDao(sqlUse.toString(), sql.getParamters());
    	return jdbcDao.query(sqlDao,new EntityMapper<T>(meta, clazz));
        }
    
    public <E extends AbstractTableEntity> List<E> getListByOr(E e){
    	Assert.notNull(e, ENTITY_NULL_MSG);
        AbstractTableEntityMeta meta = EntityManager.getEntityMeta(e.getClass());
        Assert.notNull(meta, e.getClass().getSimpleName() + META_NULL_MSG);

        IParameters params = Parameters.create();
        String sql = this.getOrConditionSql(e, meta, params);
        String qrySql = new StringBuffer(meta.getSelectSql())
        		.append(" where ").append(sql).toString();
		return jdbcDao.query(qrySql.toString(), params, new EntityMapper<E>(meta, null));
    }
    
	private String getOrConditionSql(Object e, AbstractTableEntityMeta meta, IParameters params) {
		StringBuffer sql = new StringBuffer();

		List<ColumnInfo> columns = meta.getColumns();
		for (ColumnInfo column : columns) {
			Object value = this.readParam(e, column.getPd());
			if(value != null){
				if(sql.length()>0){
					sql.append(" or ");
				}
				sql.append(column.getColName()).append("=?");
                params.add(value);
			}
		}
		if(params.isEmpty()){
			throw new YDException(meta.getTableName() +" sql 参数为空!");
		}
		return sql.toString();
	}
}
