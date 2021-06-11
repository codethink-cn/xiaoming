package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

/**
 * @author Chuanwise
 */
@Getter
public class GroupXiaomingUserImpl extends XiaomingUserImpl<GroupContact, GroupMessage, GroupReceptionTask> implements GroupXiaomingUser {
    final GroupContact contact;
    final TempContact tempContact;
    final List<GroupMessage> recentMessages;

    @Setter
    GroupReceptionTask receptionTask;

    public GroupXiaomingUserImpl(GroupContact contact, TempContact tempContact, List<GroupMessage> recentMessages) {
        super(contact.getXiaomingBot(), tempContact.getCode());
        this.contact = contact;
        this.tempContact = tempContact;
        this.recentMessages = recentMessages;
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new GroupMessageImpl(this, messages));
    }

    @Override
    public long getCode() {
        return tempContact.getCode();
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        contact.atSend(getCode(), replaceArguments(message, arguments));
    }

    @Override
    public void sendPrivateMessage(String message, Object... arguments) {
        tempContact.send(replaceArguments(message, arguments));
    }

    @Override
    public String getName() {
        return tempContact.getName();
    }

    @Override
    public String getCompleteName() {
        return contact.getCompleteName() + getName() + "(" + getCodeString() + ")";
    }
}
