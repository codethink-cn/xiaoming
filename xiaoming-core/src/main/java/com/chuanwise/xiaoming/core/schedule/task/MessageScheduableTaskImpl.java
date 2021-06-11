package com.chuanwise.xiaoming.core.schedule.task;

import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.schedule.task.MessageTimeTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.MessageChain;

@Data
@AllArgsConstructor
public class MessageScheduableTaskImpl extends ScheduableTaskImpl implements MessageTimeTask {
    long group;
    long qq;
    String message;

    public MessageScheduableTaskImpl() {
        setCallable(() -> {
            final Bot miraiBot = getXiaomingBot().getMiraiBot();
            final Receptionist receptionist = getXiaomingBot().getReceptionistManager().getOrPutReceptionist(qq);
            if (group == 0) {
                receptionist.onPrivateMessage(getXiaomingBot().getContactManager().getPrivateContact(qq), MessageChain.deserializeFromJsonString(message));
            } else {
                receptionist.onGroupMessage(getXiaomingBot().getContactManager().getGroupContact(group), MessageChain.deserializeFromJsonString(message));
            }
            return true;
        });
    }
}
