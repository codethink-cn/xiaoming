package cn.codethink.xiaoming.test;

import cn.chuanwise.common.util.Files;
import cn.chuanwise.common.util.Numbers;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.MiraiBotFactory;

import java.io.File;
import java.io.IOException;

public class MessageEventHandlerTest {
    
    @SuppressWarnings("all")
    public static void main(String[] args) throws IOException {
        final String[] strings = Files.readString(new File("test/qq.txt")).split("\\n");
        
        final long qq = Numbers.parseLong(strings[0]);
        final String password = strings[1];
    
        // create bot
        final Bot bot = MiraiBotFactory.newBot(qq, password);
        final File workingDirectory = new File("test/bot");
        workingDirectory.mkdirs();
        bot.getBotConfiguration().setWorkingDirectory(workingDirectory);
    
        if (bot.start()) {
            doTest(bot);
        } else {
            System.err.println("can not start bot!");
        }
    }
    
    public static void doTest(Bot bot) {
    
    }
}
