package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.account.record.GroupCommandRecord;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
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

    public GroupXiaomingUserImpl(GroupContact contact, MemberContact memberContact, List<GroupMessage> recentMessages) {
        super(contact.getXiaomingBot(), memberContact.getCode());
        this.contact = contact;
        this.memberContact = memberContact;
        this.recentMessages = recentMessages;
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new GroupMessageImpl(this, messages));
    }

    @Override
    public void onNextInput(GroupMessage message) {
        final List<GroupMessage> list = getRecentMessages();
        setProperty("last", message.serialize());
        list.add(message);

        final Receptionist receptionist = getReceptionist();
        receptionist.setGlobalRecentMessages(list);

        final GroupReceptionTask receptionTask = getReceptionTask();
        if (Objects.isNull(receptionTask)) {
            receptionist.onGroupMessage(getContact(), message);
        }

        getOrPutAccount().addCommand(new GroupCommandRecord(getGroupCode(), message.serialize()));

        synchronized (list) {
            list.notifyAll();
        }
        synchronized (this) {
            this.notifyAll();
        }
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
        return contact.getCompleteName() + getName() + "(" + getCodeString() + ")";
    }
}
