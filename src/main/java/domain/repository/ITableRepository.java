package domain.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import domain.entity.AbstractTableEntity;
import jdbc.IParameters;

/**
 * 统一的表类实体仓储接口
 * 
 * @author wangpeng
 *
 */
public interface ITableRepository {
    /**
     * 新增
     * 
     * @param e
     *            表实体对象
     * @return 插入记录数
     */
    public <E extends AbstractTableEntity> int insert(E e);
    
    /**
     * 修改（更新非null字段），必须要提供主键
     * 
     * @param e
     *            表实体对象
     * @return 更新记录数
     */
    public <E extends AbstractTableEntity> int update(E e);
    
    
    /**
     * 删除，必须要传主键
     * 
     * @param e
     *            表实体对象
     * @return 删除记录数
     */
    public <E extends AbstractTableEntity> int deleteByPK(E e);
    
    /**
     * 删除，不需要主键
     * 
     * @param e
     *            表实体对象
     * @return 删除记录数
     */
    public <E extends AbstractTableEntity> int deleteByNoPK(E e);
    
    
    /**
     * 查询
     * @param e
     *            表实体对象
     * @return 实体记录
     */
    public <E extends AbstractTableEntity> E get(E e);
    
    /**
     * 查询
     * @param e
     *            表实体对象集合
     * @return 实体记录
     */
    public <E extends AbstractTableEntity> List<E> getList(E e);
    
    /**
     * 查询序列
     * 
     * @param seqName 序列
     * @return 一个序列值
     */
    public long getSeqID();
    
    
    /**
     * 获取数据库时间
     * 
     * @return 数据库时间
     */
    public Date getSysDate();
    
    /**
     * 通过自定义查询语句
     * @param e
     *            表实体对象集合
     * @return 实体记录
     */
    public <E extends AbstractTableEntity> List<E> getList(String sql, IParameters params,E e);
    
    /**
     * 通过自定义的参数查询，支持in,or,limit
     * @param e
     *            表实体对象集合
     * @return 实体记录
     */
    public <E extends AbstractTableEntity> List<E> getList(Class<E> tab, Map<String, Object> params);
    
    /**
     * 通过or条件查询
     * @param e
     *            表实体对象集合
     * @return 实体记录
     */
    public <E extends AbstractTableEntity> List<E> getListByOr(E e);
    
}
