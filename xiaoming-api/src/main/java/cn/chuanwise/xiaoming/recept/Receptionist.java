package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.property.PropertyHandler;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;

import java.util.Map;

/**
 * 小明接待员
 */
public interface Receptionist extends ModuleObject, PropertyHandler {
    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    Map<Long, GroupXiaomingUser> getGroupXiaomingUsers();

    Map<Long, MemberXiaomingUser> getMemberXiaomingUsers();

    PrivateXiaomingUser getPrivateXiaomingUser();

    GroupXiaomingUser getGroupXiaomingUser(long groupCode);

    MemberXiaomingUser getMemberXiaomingUser(long code);
}