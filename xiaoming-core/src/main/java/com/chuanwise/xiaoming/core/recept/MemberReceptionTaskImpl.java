package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import com.chuanwise.xiaoming.api.account.record.MemberCommandRecord;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.recept.MemberReceptionTask;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberReceptionTaskImpl extends ReceptionTaskImpl implements MemberReceptionTask {
    final MemberXiaomingUser user;
    final List<MemberMessage> recentMessages;

    protected MemberReceptionTaskImpl(MemberXiaomingUser user, MemberMessage message) {
        super(user.getReceptionist(), "reception-task[" + user.getCompleteName() + "]", message);
        this.user = user;
        user.setReceptionTask(this);
        this.recentMessages = user.getRecentMessages();
    }

    @Override
    protected void register() {
        thread = Thread.currentThread();
        thread.setName(identify);
        receptionist.getMemberTasks().put(getUser().getGroupContact().getCode(), this);
    }

    @Override
    protected void unregister() {
        receptionist.getMemberTasks().remove(getUser().getGroupContact().getCode());
    }
}
