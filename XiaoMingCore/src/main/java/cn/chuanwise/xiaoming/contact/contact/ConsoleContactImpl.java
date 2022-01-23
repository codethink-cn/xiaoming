package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.exception.IllegalOperationException;
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
import java.util.Optional;
import java.util.concurrent.Future;

@Getter
public class ConsoleContactImpl extends XiaomingContactImpl<Friend> implements ConsoleContact {
    final ConsoleInputThread thread;

    public ConsoleContactImpl(XiaomingBot xiaomingBot, ConsoleInputThread thread) {
        super(xiaomingBot, xiaomingBot.getMiraiBot().getAsFriend());
        this.thread = thread;
    }

    @Override
    public Optional<Message> sendMessage(MessageChain messages) {
        xiaomingBot.getConsoleXiaomingUser().getLogger().info(messages.serializeToMiraiCode());
        return Optional.of(new MessageImpl(xiaomingBot, messages));
    }

    @Override
    public void flush() {}

    @Override
    public boolean addTag(String tag) {
        throw new IllegalOperationException();
    }

    @Override
    public boolean hasTag(String tag) {
        throw new IllegalOperationException();
    }

    @Override
    public boolean removeTag(String tag) {
        throw new IllegalOperationException();
    }
}