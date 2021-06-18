package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.account.record.GroupCommandRecord;
import com.chuanwise.xiaoming.api.account.record.MemberCommandRecord;
import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.recept.MemberReceptionTask;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.core.contact.message.MemberMessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;

@Getter
public class MemberXiaomingUserImpl extends XiaomingUserImpl<MemberContact, MemberMessage, MemberReceptionTask> implements MemberXiaomingUser {
    final MemberContact contact;
    final List<MemberMessage> recentMessages;

    @Setter
    MemberReceptionTask receptionTask;

    public MemberXiaomingUserImpl(MemberContact memberContact, List<MemberMessage> recentMessages) {
        super(memberContact.getXiaomingBot(), memberContact.getCode());
        this.contact = memberContact;
        this.recentMessages = recentMessages;
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new MemberMessageImpl(this, messages));
    }

    @Override
    public void onNextInput(MemberMessage message) {
        final List<MemberMessage> list = getRecentMessages();
        setProperty("last", message.serialize());
        list.add(message);

        final Receptionist receptionist = getReceptionist();
        receptionist.setGlobalRecentMessages(list);

        final MemberReceptionTask receptionTask = getReceptionTask();
        if (Objects.isNull(receptionTask)) {
            receptionist.onMemberMessage(getContact(), message);
        }

        getOrPutAccount().addCommand(new MemberCommandRecord(getGroupCode(), message.serialize()));

        synchronized (list) {
            list.notifyAll();
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public long getCode() {
        return contact.getCode();
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        if (isUsingBuffer()) {
            appendBuffer(replacedMessage);
        } else {
            contact.send(replacedMessage);
        }
    }

    @Override
    public String getName() {
        return contact.getName();
    }

    @Override
    public String getCompleteName() {
        return contact.getCompleteName();
    }
}
