package ${pack};
import java.util.List;

/**
 * ${table}表
 *
 * @author system
 *
 */
public interface ${objectName}Mapper {
     /**
      物理删除
     */
    int deleteByPrimaryKey(${idbeanModel.javaType}  ${idbeanModel.javaName});
     /**
      根据主键查询
     */
    ${objectName} selectByPrimaryKey(${idbeanModel.javaType}  ${idbeanModel.javaName});
    /**
      插入
     */
    int insert(${objectName} record);
     /**
     条件 插入
     */
    int insertSelective(${objectName} record);
    /**
     条件 更新
     */
   int updateByPrimaryKeySelective(${objectName} record);
   /**
     主键 更新
     */
   int updateByPrimaryKey(${objectName} record);
   /**
     条件统计
     */
   int searchByEntityCount(${objectName} record);
   /**
     条件查询
     */
   List<${objectName}> searchByEntity(${objectName} record);
}