package cn.chuanwise.xiaoming.event;

import cn.chuanwise.toolkit.box.Box;
import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import lombok.Data;
import net.mamoe.mirai.message.data.MessageChain;

@Data
public class SendMessageEvent
        extends SimpleXiaomingCancellableEvent {
    final XiaomingContact target;
    final MessageChain messageChain;

    final long time = System.currentTimeMillis();
    final Box<Message> messageBox = Box.empty();
}