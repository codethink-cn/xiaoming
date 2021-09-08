package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.thread.ConsoleInputThread;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ConsoleContactImpl extends XiaomingContactImpl<Friend> implements ConsoleContact {
    final ConsoleInputThread thread;

    public ConsoleContactImpl(XiaomingBot xiaomingBot, ConsoleInputThread thread) {
        super(xiaomingBot, xiaomingBot.getMiraiBot().getAsFriend());
        this.thread = thread;
    }

    @Override
    public Message sendMessage(MessageChain messages) {
        xiaomingBot.getConsoleXiaomingUser().getLogger().info(messages.serializeToMiraiCode());
        return new MessageImpl(xiaomingBot, messages);
    }
}