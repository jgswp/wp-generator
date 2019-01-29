
package bean;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 异常类
 * 
 * @author cdq
 */
public final class YDException extends RuntimeException {
    private static final long   serialVersionUID   = 3856935301311065813L;
    
    private String errorCode;//暂无自定义编码，有需求再加
    private static final String CAUSE_CAPTION      = "Caused by: ";
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";
    
    public String getErrorCode() {
    	return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
    	this.errorCode = errorCode;
    }
    
    public YDException(String errorMassage) {
		super(errorMassage);
	}
    
    public YDException(String errorCode,String errorMassage) {
		super(errorMassage);
		this.errorCode = errorCode;
	}

    public YDException(String errorCode,String errorMassage,Throwable cause) {
		super(errorMassage,cause);
		this.errorCode = errorCode;
	}
    
	protected YDException() {
        super();
    }
   
    public YDException(String errorMassage,Throwable cause) {
        super(errorMassage,cause);
    }
    
    public final static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        pw.println(t);
        
        Set<StackTraceElement> traceVu = Collections
            .newSetFromMap(new IdentityHashMap<StackTraceElement, Boolean>());
        
        int rowCount = 0;
        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            String className = traceElement.getClassName();
            if (rowCount < 3 || isPrint(className)) {
                pw.println("\tat " + traceElement);
                ++rowCount;
            }
            
            traceVu.add(traceElement);
        }
        
        Set<Throwable> dejaVu = Collections
            .newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(t);
        
        for (Throwable se : t.getSuppressed()) {
            printEnclosedStackTrace(se, pw, traceVu, SUPPRESSED_CAPTION, "\t", dejaVu);
        }
        
        Throwable ourCause = t.getCause();
        if (ourCause != null) {
            printEnclosedStackTrace(ourCause, pw, traceVu, CAUSE_CAPTION, "", dejaVu);
        }
        
        return sw.toString();
    }
    
    private static void printEnclosedStackTrace(Throwable se, PrintWriter pw,
            Set<StackTraceElement> traceVu, String caption, String prefix, Set<Throwable> dejaVu) {
            if (dejaVu.contains(se)) {
                pw.println("\t[CIRCULAR REFERENCE:" + se + "]");
            } else {
                dejaVu.add(se);
                
                StackTraceElement[] trace = se.getStackTrace();
                int rowCount = 0;
                pw.println(prefix + caption + se);
                for (StackTraceElement traceElement : trace) {
                    String className = traceElement.getClassName();
                    if (rowCount < 3 || (!traceVu.contains(traceElement) && isPrint(className))) {
                        pw.println(prefix + "\tat " + traceElement);
                        ++rowCount;
                    }
                    
                    traceVu.add(traceElement);
                }
                
                for (Throwable sc : se.getSuppressed()) {
                    printEnclosedStackTrace(sc, pw, traceVu, SUPPRESSED_CAPTION, prefix + "\t", dejaVu);
                }
                
                Throwable ourCause = se.getCause();
                if (ourCause != null) {
                    printEnclosedStackTrace(ourCause, pw, traceVu, CAUSE_CAPTION, prefix, dejaVu);
                }
            }
        }
    
    private final static boolean isPrint(String className) {
        if (className.startsWith("com.eshore") //
            || className.startsWith("com.ctg") //
            || className.startsWith("com.mysql") //
        ) {
            return true;
        }
        return false;
    }
}
