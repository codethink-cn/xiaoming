package com.chuanwise.xiaoming.core.contact.contact;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.core.thread.ConsoleInputThread;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class ConsoleContactImpl extends XiaomingContactImpl implements ConsoleContact {
    final ConsoleInputThread thread;
    final List<ConsoleMessage> recentMessages = new ArrayList<>();

    public ConsoleContactImpl(XiaomingBot xiaomingBot, ConsoleInputThread thread) {
        super(xiaomingBot);
        this.thread = thread;
    }

    @Override
    public void send(MessageChain messages) {
        thread.getLog().info(messages.serializeToMiraiCode());
    }
}
