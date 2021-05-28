package com.chuanwise.xiaoming.core.time.task;

import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.time.task.MessageTimeTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class MessageTimeTaskImpl extends TimeTaskImpl implements MessageTimeTask {
    long group;
    long qq;
    String message;

    @Override
    public void run() {
        try {
            final Bot miraiBot = getXiaomingBot().getMiraiBot();
            if (group == 0) {
                final Friend friend = miraiBot.getFriend(qq);
                final Receptionist receptionist = getXiaomingBot().getReceptionistManager().getOrPutReceptionist(qq);
                receptionist.onPrivateMessage(friend, message);
            } else {
                final Group group = miraiBot.getGroup(this.group);
                final NormalMember member = group.get(qq);
                final Receptionist receptionist = getXiaomingBot().getReceptionistManager().getOrPutReceptionist(qq);
                receptionist.onGroupMessage(member, message);
            }
            access = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
