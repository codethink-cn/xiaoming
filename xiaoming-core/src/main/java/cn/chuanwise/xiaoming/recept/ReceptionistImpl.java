package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.util.MapUtil;
import cn.chuanwise.util.ObjectUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.user.*;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.user.GroupXiaomingUserImpl;
import cn.chuanwise.xiaoming.user.MemberXiaomingUserImpl;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUserImpl;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 小明接待员
 * @author Chuanwise
 */
@Getter
public class ReceptionistImpl
        extends ModuleObjectImpl
        implements Receptionist {
    final long code;

    public ReceptionistImpl(XiaomingBot xiaomingBot, long code) {
        super(xiaomingBot);
        this.code = code;

        final Configuration configuration = xiaomingBot.getConfiguration();
        this.groupXiaomingUsers = new SizedResidentConcurrentHashMap<>(configuration.getMaxGroupUserQuantityInReceptionist());
        this.memberXiaomingUsers = new SizedResidentConcurrentHashMap<>(configuration.getMaxMemberUserQuantityInReceptionist());
        this.properties = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxUserAttributeQuantity());
        this.conditionalVariables = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxUserAttributeQuantity());
    }

    /** 私聊接待线程任务 */
    @Setter
    volatile ReceptionTask<PrivateXiaomingUser> privateTask;

    final Map<Long, GroupXiaomingUser> groupXiaomingUsers;

    final Map<Long, MemberXiaomingUser> memberXiaomingUsers;

    PrivateXiaomingUser privateXiaomingUser;

    final Map<PropertyType, Object> properties;
    final Map<PropertyType, Object> conditionalVariables;

    @Override
    public Map<PropertyType, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public <T> Container<T> removeProperty(PropertyType<T> type) {
        return Container.of((T) properties.remove(type));
    }

    @Override
    public <T> void setProperty(PropertyType<T> type, T value) {
        synchronized (properties) {
            synchronized (conditionalVariables) {
                properties.put(type, value);

                // 唤醒那些正在等待的线程
                final Object conditionalVariable = conditionalVariables.get(type);
                if (Objects.nonNull(conditionalVariable)) {
                    synchronized (conditionalVariable) {
                        conditionalVariable.notifyAll();
                    }
                    conditionalVariables.remove(type);
                }
            }
        }
    }

    @Override
    public <T> Container<T> waitProperty(PropertyType<T> type, long timeout) throws InterruptedException {
        final Object conditionalVariable = MapUtil.getOrPutSupply(conditionalVariables, type, Object::new);
        switch (ObjectUtil.wait(conditionalVariable, timeout)) {
            case NOTIFY:
                return Container.of((T) properties.get(type));
            case TIMEOUT:
                return Container.empty();
            default:
                throw new UnsupportedVersionException();
        }
    }

    @Override
    public GroupXiaomingUser getGroupXiaomingUser(long groupCode) {
        return MapUtil.getOrPutSupply(groupXiaomingUsers, groupCode,
                () -> {
                    final GroupXiaomingUserImpl groupXiaomingUser = new GroupXiaomingUserImpl(getXiaomingBot().getContactManager().getMemberContact(groupCode, code));
                    groupXiaomingUser.setReceptionist(ReceptionistImpl.this);
                    return groupXiaomingUser;
                });
    }

    @Override
    public MemberXiaomingUser getMemberXiaomingUser(long groupCode) {
        return MapUtil.getOrPutSupply(memberXiaomingUsers, groupCode,
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
