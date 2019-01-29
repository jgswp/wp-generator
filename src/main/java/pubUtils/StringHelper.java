package pubUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * String工具类
 * 
 * @author cdq
 */
public class StringHelper {
    /**
     * 空字串
     */
    public static final String EMPTY = "";
    
    /**
     * 检测是否为空字符串或为null
     * 
     * @param str
     *            待检测的字符串
     * @return 若str为null或空字符串则返回true; 否则返加false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() <= 0;
    }
    
    /**
     * 检测是否不为空字符串且不为null
     * 
     * @param str
     *            待检测的字符串
     * @return 若str不是null且不是空字符串则返回true; 否则返加false
     */
    public static boolean isNotEmpty(String str) {
        return (str != null && str.length() > 0);
    }
    
    /**
     * 检测是否为空格串、空字符串或null
     * 
     * @param str
     *            待检测的字符串
     * @return 若str为null或空字符串则或空格串返回true; 否则返加false
     */
    public static boolean isBlank(String str) {
        if (isEmpty(str)) {
            return true;
        }
        
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检测是否为空格串、空字符串或null
     * 
     * @param str
     *            待检测的字符串
     * @return 若str为null或空字符串则或空格串返回false; 否则返加true
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 
     * 二进制转换为十六进制String
     * 
     * @param buff
     *            二进制
     * 
     * @return 十六进制String
     * 
     */
    public static String byte2hex(byte[] buff) {
        if (buff == null || buff.length <= 0) {
            return EMPTY;
        }
        
        StringBuilder hexStr = new StringBuilder();
        String tmpStr = null;
        for (int n = 0; n < buff.length; n++) {
            tmpStr = (java.lang.Integer.toHexString(buff[n] & 0XFF));
            if (tmpStr.length() == 1) {
                hexStr.append("0");
            }
            hexStr.append(tmpStr);
        }
        return hexStr.toString().toUpperCase();
    }
    
    /**
     * 
     * 十六进制String转换为字节数组
     * 
     * @param buff
     *            二进制
     * 
     * @return 十六进制String
     * 
     */
    public static byte[] hex2byte(String hexstr) {
        if (isBlank(hexstr)) {
            return null;
        }
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }
    
    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }
    
    /**
     * 用StringTokenizer分割字符串到数组
     * 
     * @param str
     *            待分割字符串
     * @param sep
     *            分割符
     * @return 分割后的字符串数组
     */
    public static String[] splitTokens(String str, String sep) {
        if (isEmpty(str)) {
            return null;
        }
        
        StringTokenizer token = new StringTokenizer(str, sep);
        int count = token.countTokens();
        if (count < 1) {
            return null;
        }
        
        int i = 0;
        String[] output = new String[count];
        while (token.hasMoreTokens()) {
            output[i] = token.nextToken();
            ++i;
        }
        return output;
    }
    
    /**
     * 用StringTokenizer分割字符串到数组
     * 
     * @param str
     *            待分割字符串
     * @param sep
     *            分割符
     * @return 分割后的字符串set容器
     */
    public static Set<String> splitToSet(String str, String sep) {
        Set<String> output = new HashSet<String>();
        if (isEmpty(str)) {
            return output;
        }
        
        StringTokenizer token = new StringTokenizer(str, sep);
        while (token.hasMoreTokens()) {
            output.add(token.nextToken());
        }
        
        return output;
    }
    
    
    /**
     * 将容器的字符串/数组连接起来
     * 
     * @param collection
     *            容器
     * @param sep
     *            分割符
     * @return 字符串
     */
    public static String join(Collection<?> collection, String sep) {
        if (collection == null) {
            return null;
        }
        StringBuilder outStr = new StringBuilder();
        for (Object obj : collection) {
            if (obj == null) {
                continue;
            }
            if (outStr.length() > 0) {
                outStr.append(sep);
            }
            outStr.append(obj);
        }
        return outStr.toString();
    }
    
    /**
     * 将容器的字符串/数组连接起来
     * 
     * @param set
     *            容器
     * @param collection
     *            分割符
     * @param mark
     *            分割字符串的前后增加mark标签符
     * @return 字符串
     */
    public static String join(Collection<?> collection, String sep, char mark) {
        StringBuilder outStr = new StringBuilder();
        for (Object obj : collection) {
            if (obj == null) {
                continue;
            }
            if (outStr.length() > 0) {
                outStr.append(sep);
            }
            outStr.append(mark).append(obj).append(mark);
        }
        return outStr.toString();
    }
    
    /**
     * 对空串的处理
     * 
     * @param input
     *            ：输入的字符串
     * @param def
     *            ：为空返回值
     * @return 如果输入null或空字符串返回def，否则返回trim后字符串
     */
    public static String handleNull(String input, String def) {
        if (isEmpty(input)) {
            return def;
        }
        
        String trimStr = input.trim();
        if (isEmpty(trimStr)) {
            return def;
        }
        
        return trimStr;
    }
    
    /**
     * 判断两个字符串是否相等（大小写敏感）
     * 
     * <pre>
     * StringHelper.equals(null, null)   = true
     * StringHelper.equals(null, &quot;abc&quot;)  = false
     * StringHelper.equals(&quot;abc&quot;, null)  = false
     * StringHelper.equals(&quot;abc&quot;, &quot;abc&quot;) = true
     * StringHelper.equals(&quot;abc&quot;, &quot;ABC&quot;) = false
     * </pre>
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null
            : str1.equals(str2);
    }
    
    /**
     * 判断两个字符串是否相等（不区分大小写）
     * 
     * <pre>
     * StringHelper.equalsIgnoreCase(null, null)   = true
     * StringHelper.equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * StringHelper.equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * StringHelper.equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * StringHelper.equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null
            : str1.equalsIgnoreCase(str2);
    }
    
    /**
     * 去掉字符串中所有的空格/回车/TAB
     * 
     * @param str
     *            输入的字符串
     * @return 去掉字符串中所有空格/回车/TAB的字符串
     */
    public static String trimAll(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        StringBuffer buf = new StringBuffer(str);
        int index = 0;
        while (buf.length() > index) {
            char c = buf.charAt(index);
            if (Character.isWhitespace(c) || c == '\t' || c == '\r' || c == '\n') {
                buf.deleteCharAt(index);
            } else {
                ++index;
            }
        }
        
        return buf.toString();
    }
    
    /**
     * 去掉字符串中前后的空格/回车/TAB
     * 
     * @param str
     *            输入的字符串
     * @return 去掉前后空格/回车/TAB的字符串
     */
    public static String trimMore(String str) {
        if (StringHelper.isEmpty(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        
        // 去掉头部的空格
        int index = 0;
        while (buf.length() > index) {
            char c = buf.charAt(index);
            if (Character.isWhitespace(c) || c == '\t' || c == '\r' || c == '\n') {
                buf.deleteCharAt(index);
            } else {
                break;
            }
        }
        
        // 去掉尾部的空格
        while (buf.length() > 0) {
            int len = buf.length() - 1;
            char c = buf.charAt(len);
            if (Character.isWhitespace(c) || c == '\t' || c == '\r' || c == '\n') {
                buf.deleteCharAt(len);
            } else {
                break;
            }
        }
        
        return buf.toString();
    }
    
    /**
     * 去掉字符串中前后的空格
     * 
     * @param str
     *            输入的字符串
     * @return 返回去空格后的字符串
     */
    public static String trim(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.trim();
    }
    
    /**
     * 第一个字母转大写
     * 
     * @param str
     *            输入的字符串
     * @return 首字母为大写的字符串
     */
    public static String first2Upper(String str) {
        if (StringHelper.isEmpty(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 第一个字母变小写
     * 
     * @param str
     *            输入的字符串
     * @return 首字母为小写的字符串
     */
    public static String first2Lower(String str) {
        if (StringHelper.isEmpty(str)) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 根据字段名获取属性名
     * 
     * @param columnName
     *            字段名
     * @return 属性名
     */
    public static String columnName2propertyName(String columnName) {
        StringBuilder sb = new StringBuilder();
        String fieldName = columnName.toLowerCase();
        StringTokenizer token = new StringTokenizer(fieldName, "_");
        while (token.hasMoreTokens()) {
            sb.append(StringHelper.first2Upper(token.nextToken()));
        }
        return StringHelper.first2Lower(sb.toString());
    }
    
    /**
     * 将属性名改为数据库表的字段名，如attrCode改为ATTR_CODE
     * 
     * @param propertyName
     *            属性名
     * @return 字段名
     */
    public static String propertyName2ColumnName(String propertyName) {
        StringBuffer sb = new StringBuffer();
        char c = 0;
        char[] chs = propertyName.toCharArray();
        int len = chs.length;
        for (int i = 0; i < len; i++) {
            c = chs[i];
            if (Character.isUpperCase(c)) {// 大写
                sb.append("_");
                sb.append(Character.toLowerCase(c));// 转小写
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
