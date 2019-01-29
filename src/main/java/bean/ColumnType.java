package bean;

/**
 * 字段类型
 * @author cdq
 */
public enum ColumnType {
    /** 主键 */
    PK
    /** 启用的分片键 */
    , SHARD
    /** 为启用的分片键，缺省值为-1 */
    , SHARD_UNUSED
    /** 其他字段 */
    ,COL
}
