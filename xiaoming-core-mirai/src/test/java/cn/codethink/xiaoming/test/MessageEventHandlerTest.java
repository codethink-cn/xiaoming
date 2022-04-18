package cn.codethink.xiaoming.test;

import cn.chuanwise.common.util.Files;
import cn.chuanwise.common.util.Numbers;
import cn.chuanwise.common.util.Strings;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.MiraiBotFactory;
import cn.codethink.xiaoming.annotation.EventHandler;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.event.GroupMessageRecallEvent;
import cn.codethink.xiaoming.event.ReceiveGroupMessageEvent;
import cn.codethink.xiaoming.message.basic.AccountMention;
import cn.codethink.xiaoming.message.metadata.MessageMetadataType;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.message.metadata.MessageSource;
import cn.codethink.xiaoming.message.metadata.Quote;
import cn.codethink.xiaoming.spi.XiaoMingImpl;
import cn.codethink.xiaoming.spi.XiaoMingSpi;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class MessageEventHandlerTest {
    
    @SuppressWarnings("all")
    public static void main(String[] args) throws Throwable {
        final String string = Files.readString(new File("test/qq.txt"));
    
        final Class<XiaoMingImpl> xiaoMingClass = XiaoMingImpl.class;
        System.out.println(XiaoMingImpl.class.getName());
        XiaoMingSpi.setXiaoMing(new XiaoMingImpl());
    
        final String[] strings = Stream.of(string.split("\\s"))
            .filter(Strings::nonEmpty)
            .toArray(String[]::new);
        
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
    
    public static void doTest(Bot bot) throws Throwable {
        System.out.println("bot started!");
        
        bot.getEventManager().registerListeners(new Object() {
            
            @EventHandler
            void handleGroupMessageEvent(ReceiveGroupMessageEvent event) {
                final CompoundMessage message = event.getMessage();
                final GroupSender sender = event.getSender();
    
                if (sender instanceof UserOrBot && ((UserOrBot) sender).getCode().asLong() != 1437100907) {
                    return;
                }
    
                final MessageSource reference = message.getMetadataOrFail(MessageMetadataType.SOURCE);
                final CompoundMessage compoundMessage = Quote.of(reference)
                    .plus(message)
                    .asBuilder()
//                    .plus(AccountMention.singleton(groupSender.getCode()))
                    .build();
                
                event.getTarget().sendMessage(compoundMessage);
            }
            
            @EventHandler
            void handleGroupMessageRecallEvent(GroupMessageRecallEvent event) {
                final CompoundMessage message = event.getMessage();
                final GroupMember sender = (GroupMember) event.getSender();
    
                if (sender.getCode().asLong() != 1437100907) {
                    return;
                }
    
                final Group group = event.getTarget();
                final CompoundMessageBuilder messageBuilder = CompoundMessageBuilder.newInstance()
                    .plus(AccountMention.singleton(sender.getCode()))
                    .plus("你刚才是不是撤回了一条消息");
    
                if (Objects.isNull(message)) {
                    messageBuilder.plus("，但是消息找不到了。");
                } else {
                    messageBuilder.plus("：")
                        .plus(message);
                }
                
                group.sendMessage(messageBuilder);
            }
        });
    
        TimeUnit.DAYS.sleep(1);
    }
}
