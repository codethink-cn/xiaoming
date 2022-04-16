package cn.codethink.xiaoming.contact;

/**
 * 集体成员
 *
 * @author Chuanwise
 */
public interface Member
    extends User, MassSender, Cached, Contact {
    
    /**
     * 获取加入集体的时间戳
     *
     * @return 加入集体的时间戳
     */
    long getJoinTimestamp();
}
