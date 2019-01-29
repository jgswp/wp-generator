package pubUtils.template;

import java.util.List;

public class TableInfo {
    private String           tableName;             // 表名
    private String           className;             // bean类名
    private boolean          isBranchTable  = false;
    private String           branchTableCol = "";
    private String           packageName;           // bean所在包位置
    private int              dateColNum     = 0;    // 日期字段数量
    private int              decimalColNum     = 0;    // 日期字段数量
    private List<ColumnInfo> allCols;
    private String           entityUUID;

    public int getDecimalColNum() {
        return decimalColNum;
    }

    public void setDecimalColNum(int decimalColNum) {
        this.decimalColNum = decimalColNum;
    }

    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public boolean isBranchTable() {
        return isBranchTable;
    }
    
    public void setBranchTable(boolean isBranchTable) {
        this.isBranchTable = isBranchTable;
    }
    
    public String getBranchTableCol() {
        return branchTableCol;
    }
    
    public void setBranchTableCol(String branchTableCol) {
        this.branchTableCol = branchTableCol;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public int getDateColNum() {
        return dateColNum;
    }
    
    public void setDateColNum(int dateColNum) {
        this.dateColNum = dateColNum;
    }
    
    public List<ColumnInfo> getAllCols() {
        return allCols;
    }
    
    public void setAllCols(List<ColumnInfo> allCols) {
        this.allCols = allCols;
    }
    
    public String getEntityUUID() {
        return entityUUID;
    }
    
    public void setEntityUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }
    
}
