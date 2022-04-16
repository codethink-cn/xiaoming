package cn.codethink.xiaoming.contact;

/**
 * 可以在群聊内发送消息的人。
 * 可以是匿名集体成员 {@link AnonymousMassSender} 或实名集体成员 {@link Member}。
 *
 * @author Chuanwise
 */
public interface GroupSender
    extends MassSender {
    
    /**
     * 获取所在的集体
     *
     * @return 成员所在的集体
     */
    @Override
    Group getMass();
}
