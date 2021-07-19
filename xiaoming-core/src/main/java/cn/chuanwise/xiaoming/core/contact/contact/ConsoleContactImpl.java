package cn.chuanwise.xiaoming.core.contact.contact;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.core.contact.message.ConsoleMessageImpl;
import cn.chuanwise.xiaoming.core.thread.ConsoleInputThread;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class ConsoleContactImpl extends XiaomingContactImpl<ConsoleMessage, Friend> implements ConsoleContact {
    final ConsoleInputThread thread;
    final List<ConsoleMessage> recentMessages = new ArrayList<>();

    public ConsoleContactImpl(XiaomingBot xiaomingBot, ConsoleInputThread thread) {
        super(xiaomingBot);
        this.thread = thread;
    }

    @Override
    public ConsoleMessage send(MessageChain messages) {
        log.info(messages.serializeToMiraiCode());
        return new ConsoleMessageImpl(getXiaomingBot().getConsoleXiaomingUser(), messages);
    }
}