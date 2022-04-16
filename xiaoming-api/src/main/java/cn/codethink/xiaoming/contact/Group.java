package cn.codethink.xiaoming.contact;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.exception.NoSuchAnonymousSenderException;
import cn.codethink.xiaoming.exception.NoSuchMemberException;

import java.util.Map;
import java.util.Objects;

/**
 * 像 QQ 那样的一对多的群，只有一个可以推送消息的区域。
 *
 * @author Chuanwise
 */
public interface Group
    extends Contact, Mass {
    
    /**
     * 获取群设置项
     *
     * @return 群设置项
     */
    @Override
    GroupConfiguration getConfiguration();
    
    /**
     * 获取某位群员
     *
     * @param code 成员编号
     * @return 群员
     * @throws NullPointerException code 为 null
     */
    @Override
    GroupMember getMember(Code code);
    
    @Override
    default GroupMember getMemberOrFail(Code code) {
        return (GroupMember) Mass.super.getMemberOrFail(code);
    }
    
    /**
     * 获取匿名成员
     *
     * @param code 成员编号
     * @return 匿名成员，或 null
     * @throws NullPointerException code 为 null
     */
    AnonymousGroupSender getAnonymousSender(Code code);
    
    /**
     * 获取匿名成员
     *
     * @param code 成员编号
     * @return 匿名成员
     * @throws NullPointerException           code 为 null
     * @throws NoSuchAnonymousSenderException 找不到该匿名成员
     */
    default AnonymousGroupSender getAnonymousSenderOrFail(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        final AnonymousGroupSender anonymousSender = getAnonymousSender(code);
        if (Objects.isNull(anonymousSender)) {
            throw new NoSuchAnonymousSenderException(getBot(), code);
        }
        
        return anonymousSender;
    }
    
    /**
     * 获取匿名成员
     *
     * @return 匿名成员
     */
    Map<Code, ? extends AnonymousGroupSender> getAnonymousSenders();
    
    /**
     * 获取群员
     *
     * @return 群员
     */
    @Override
    Map<Code, ? extends GroupMember> getMembers();
    
    /**
     * 获取 Bot 自己
     *
     * @return Bot 自己
     */
    @Override
    GroupMember getBotAsMember();
    
    /**
     * 获取群主
     *
     * @return 群主
     */
    @Override
    GroupMember getOwner();
}
