package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.attribute.AttributeHolder;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 小明接待员
 */
public interface Receptionist extends ModuleObject, AttributeHolder {
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