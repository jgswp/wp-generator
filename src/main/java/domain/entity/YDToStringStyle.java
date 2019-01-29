package domain.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringStyle;

public class YDToStringStyle extends ToStringStyle {
    private static final long         serialVersionUID = -8910572043503033295L;
    public static final ToStringStyle DEFAULT_STYLE    = new YDToStringStyle("yyyyMMddHHmmss");
    private String                    dateFormat;
    
    public YDToStringStyle(String dateFormat) {
        super();
        this.setUseShortClassName(true);
        this.setUseIdentityHashCode(false);
        this.dateFormat = dateFormat;
    }
    
    protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        // 增加自定义的date对象处理
        if (value != null && value instanceof Date) {
        	SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            value = df.format((Date) value);
        }
        // 后续可以增加其他自定义对象处理
        buffer.append(value);
    }
}
