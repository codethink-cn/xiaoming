package cn.codethink.xiaoming.contact;

import java.util.Objects;

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
    
    /**
     * 在集体中，如果设置了集体名片，则发送者名称优先为集体名片。
     * 如果没有设置集体名片，但是他是 bot 好友，则显示备注。
     * 如果没有备注，再显示账户名。
     *
     * @return 发送者名
     */
    @Override
    default String getSenderName() {
        String senderName = getMassNick();
        if (Objects.isNull(senderName)) {
            senderName = getRemarkName();
        }
        if (Objects.isNull(senderName)) {
            senderName = getAccountName();
        }
        return senderName;
    }
}
