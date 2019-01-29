package jdbc;

import java.util.LinkedList;

public class Parameters extends LinkedList<Object> implements IParameters {
    private static final long serialVersionUID = 2156716979982318160L;
    
    /**
     * 创建一个空查询参数集
     * 
     * @return 一个基于List的查询参数集
     */
    public static Parameters create() {
        return new Parameters();
    }
    
    public IParameters add(Object... values) {
        for (Object val : values) {
            this.add(val);
        }
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Object val : this) {
            if (str.length() > 0) {
                str.append(',');
            }
            if (val != null) {
                str.append("(").append(val.getClass().getSimpleName()).append(")");
            }
            str.append(val);
        }
        return str.toString();
    }
    
}
