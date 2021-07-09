package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import com.chuanwise.xiaoming.api.account.record.GroupCommandRecord;
import com.chuanwise.xiaoming.api.account.record.PrivateCommandRecord;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import lombok.Getter;

import java.util.List;

@Getter
public class GroupReceptionTaskImpl extends ReceptionTaskImpl implements GroupReceptionTask {
    final GroupXiaomingUser user;
    final List<GroupMessage> recentMessages;

    public GroupReceptionTaskImpl(GroupXiaomingUser user, GroupMessage message) {
        super(user.getReceptionist(), "reception-task[" + user.getCompleteName() + "]", message);
        this.user = user;
        user.setReceptionTask(this);
        this.recentMessages = user.getRecentMessages();
    }
    @Override
    protected void register() {
        thread = Thread.currentThread();
        thread.setName(identify);
        receptionist.getGroupTasks().put(getUser().getGroupCode(), this);
    }

    @Override
    protected void unregister() {
        receptionist.getGroupTasks().remove(user.getGroupCode());
    }
}
