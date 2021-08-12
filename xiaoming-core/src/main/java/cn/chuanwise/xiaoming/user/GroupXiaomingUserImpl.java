package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.account.record.GroupCommandRecord;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.recept.GroupReceptionTask;
import cn.chuanwise.xiaoming.contact.message.GroupMessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;

/**
 * @author Chuanwise
 */
@Getter
public class GroupXiaomingUserImpl extends XiaomingUserImpl<GroupContact, GroupMessage, GroupReceptionTask> implements GroupXiaomingUser {
    final GroupContact contact;
    final MemberContact memberContact;
    final List<GroupMessage> recentMessages;

    @Setter
    GroupReceptionTask receptionTask;

    public GroupXiaomingUserImpl(MemberContact contact) {
        super(contact.getXiaomingBot(), contact.getCode());
        this.contact = contact.getGroupContact();
        this.memberContact = contact;
        this.recentMessages = getXiaomingBot().getContactManager().forGroupMemberMessages(getGroupCodeString(), getCodeString());
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new GroupMessageImpl(this, messages));
    }

    @Override
    public void onNextInput(GroupMessage message) {
        final List<GroupMessage> list = getRecentMessages();
//        setProperty("last", message.serialize());

        final GroupReceptionTask receptionTask = getReceptionTask();
        if (Objects.isNull(receptionTask)) {
            receptionist.onGroupMessage(getContact(), message);
            return;
        }

        final Receptionist receptionist = getReceptionist();
        receptionist.setGlobalRecentMessages(list);
        synchronized (receptionist) {
            receptionist.notifyAll();
        }

        for (String tag : getContact().getTags()) {
            // 该成员在所有群中的消息
            final List<GroupMessage> recentMessages = receptionist.forGroupRecentMessages(tag);
            recentMessages.add(message);
            synchronized (recentMessages) {
                recentMessages.notifyAll();
            }

            // 群聊所有消息
            final List<GroupMessage> groupMessages = getXiaomingBot().getContactManager().forGroupMessages(tag);
            groupMessages.add(message);
            synchronized (groupMessages) {
                groupMessages.notifyAll();
            }
        }

        getAccount().addCommand(new GroupCommandRecord(getGroupCode(), message.serialize()));
    }

    @Override
    public long getCode() {
        return memberContact.getCode();
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        if (isUsingBuffer()) {
            appendBuffer(replacedMessage);
        } else {
            contact.atSend(getCode(), replacedMessage);
        }
    }

    @Override
    public String getName() {
        return memberContact.getName();
    }

    @Override
    public String getCompleteName() {
        return "「" + contact.getCompleteName() + "」" + getName() + "（" + getCodeString() + "）";
    }
}
