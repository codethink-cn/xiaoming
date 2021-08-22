package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.account.record.GroupCommandRecord;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
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
