package jdbc.sequence;

public class SequenceUtil {

    private static Sequence requence;

    public SequenceUtil(Sequence requence) {
        if(requence == null ){
            requence = new Sequence(0,0);
        }
        SequenceUtil.requence = requence;
    }

    /**
     * <p >
     * 功能：获取一个18位数字的唯一序列号
     * </p>
     * @param
     * @author loysen
     * @date  2018/1/30 9:14
     * @return
     */
    public static Long getUUid(){
        if(requence == null ){
            requence = new Sequence(0,0);
        }
        Long uUid =  requence.nextId();
        String substring = uUid.toString().substring(3);
        return Long.parseLong(substring);
    }

}
