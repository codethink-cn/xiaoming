package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Mass;
import cn.codethink.xiaoming.contact.Member;

/**
 * 和成员相关的事件
 *
 * @author Chuanwise
 */
public interface MemberEvent
    extends Event, MassEvent {
    
    /**
     * 获取成员
     *
     * @return 成员
     */
    Member getMember();
    
    /**
     * 获取成员所属的集体
     *
     * @return 成员所属的集体
     */
    @Override
    default Mass getMass() {
        return getMember().getMass();
    }
}
