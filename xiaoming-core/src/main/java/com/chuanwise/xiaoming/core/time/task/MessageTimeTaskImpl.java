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
            final Receptionist receptionist = getXiaomingBot().getReceptionistManager().getOrPutReceptionist(qq);
            if (group == 0) {
                receptionist.onPrivateMessage(getXiaomingBot().getContactManager().getPrivateContact(qq), message);
            } else {
                receptionist.onGroupMessage(getXiaomingBot().getContactManager().getGroupContact(group), message);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
