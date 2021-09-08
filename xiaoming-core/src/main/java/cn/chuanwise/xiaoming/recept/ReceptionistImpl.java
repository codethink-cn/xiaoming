package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.attribute.AttributeType;
import cn.chuanwise.xiaoming.user.*;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.user.GroupXiaomingUserImpl;
import cn.chuanwise.xiaoming.user.MemberXiaomingUserImpl;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUserImpl;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小明接待员
 * @author Chuanwise
 */
@Getter
public class ReceptionistImpl extends ModuleObjectImpl implements Receptionist {
    final long code;

    public ReceptionistImpl(XiaomingBot xiaomingBot, long code) {
        super(xiaomingBot);
        this.code = code;

        final Configuration configuration = xiaomingBot.getConfiguration();
        this.groupXiaomingUsers = new SizedResidentConcurrentHashMap<>(configuration.getMaxGroupUserQuantityInReceptionist());
        this.memberXiaomingUsers = new SizedResidentConcurrentHashMap<>(configuration.getMaxMemberUserQuantityInReceptionist());
        this.attributes = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxUserAttributeQuantity());
    }

    /** 私聊接待线程任务 */
    @Setter
    volatile ReceptionTask<PrivateXiaomingUser> privateTask;

    final Map<Long, GroupXiaomingUser> groupXiaomingUsers;

    final Map<Long, MemberXiaomingUser> memberXiaomingUsers;

    PrivateXiaomingUser privateXiaomingUser;

    @Getter
    final Map<AttributeType, Object> attributes;

    @Getter
    Map<AttributeType, Object> attributeConditionalVariables = new ConcurrentHashMap<>();

    @Override
    public GroupXiaomingUser getGroupXiaomingUser(long groupCode) {
        return MapUtility.getOrPutSupply(groupXiaomingUsers, groupCode,
                () -> {
                    final GroupXiaomingUserImpl groupXiaomingUser = new GroupXiaomingUserImpl(getXiaomingBot().getContactManager().getMemberContact(groupCode, code));
                    groupXiaomingUser.setReceptionist(ReceptionistImpl.this);
                    return groupXiaomingUser;
                });
    }

    @Override
    public MemberXiaomingUser getMemberXiaomingUser(long groupCode) {
        return MapUtility.getOrPutSupply(memberXiaomingUsers, groupCode,
                () -> {
                    final MemberXiaomingUserImpl memberXiaomingUser = new MemberXiaomingUserImpl(getXiaomingBot().getContactManager().getMemberContact(groupCode, code));
                    memberXiaomingUser.setReceptionist(ReceptionistImpl.this);
                    return memberXiaomingUser;
                });
    }

    @Override
    public PrivateXiaomingUser getPrivateXiaomingUser() {
        if (Objects.isNull(privateXiaomingUser)) {
            privateXiaomingUser = new PrivateXiaomingUserImpl(getXiaomingBot().getContactManager().getPrivateContact(getCode()));
            privateXiaomingUser.setReceptionist(this);
        }
        return privateXiaomingUser;
    }

    @Override
    public Map<Long, GroupXiaomingUser> getGroupXiaomingUsers() {
        return Collections.unmodifiableMap(groupXiaomingUsers);
    }

    @Override
    public Map<Long, MemberXiaomingUser> getMemberXiaomingUsers() {
        return Collections.unmodifiableMap(memberXiaomingUsers);
    }
}
