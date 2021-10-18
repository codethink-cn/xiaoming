package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Data;
import net.mamoe.mirai.message.code.MiraiCode;

import java.util.Optional;
import java.util.Set;

@Data
public class MessageEvent extends SimpleXiaomingCancellableEvent {
    final XiaomingUser user;
    final Message message;

    public MessageEvent(XiaomingUser user, Message message) {
        setXiaomingBot(user.getXiaomingBot());
        this.user = user;
        this.message = message;
    }
}
