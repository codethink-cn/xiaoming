package cn.codethink.xiaoming.test;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.MiraiBotFactory;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.Scope;
import net.mamoe.mirai.BotFactory;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class FriendTest {
    
    @Test
    void testSendMessage() throws InterruptedException {
        final Bot bot = MiraiBotFactory.newBot(1420431708, "lclbmiloveyou!");
    
        bot.start();

        final Scope scope = bot.getScope(Code.ofLong(1028959718));
        if (scope instanceof Group) {
            final Group group = (Group) scope;
            group.sendMessage(new Date() + "，新小明发出第二条消息").syncUninterruptibly();
        }
    
        Thread.sleep(500000);
    
        bot.stop();
    }
}
