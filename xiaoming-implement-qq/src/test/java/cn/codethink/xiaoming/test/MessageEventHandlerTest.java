package cn.codethink.xiaoming.test;

import cn.chuanwise.common.util.Files;
import cn.chuanwise.common.util.Numbers;
import cn.chuanwise.common.util.Strings;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.QqBotFactory;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.contact.GroupSender;
import cn.codethink.xiaoming.contact.UserOrBot;
import cn.codethink.xiaoming.event.ReceiveGroupMessageEvent;
import cn.codethink.xiaoming.message.basic.At;
import cn.codethink.xiaoming.message.basic.Forward;
import cn.codethink.xiaoming.message.basic.ForwardElement;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.spi.XiaoMingImpl;
import cn.codethink.xiaoming.spi.XiaoMingSpi;
import cn.codethink.xiaoming.util.MessageCode;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.LightApp;
import net.mamoe.mirai.message.data.SimpleServiceMessage;

import java.io.File;
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
        final Bot bot = QqBotFactory.newBot(qq, password);
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
        
        bot.getEventManager().registerListener(ReceiveGroupMessageEvent.class, event -> {
            final GroupSender sender = event.getSender();
            if (!(sender instanceof UserOrBot)) {
                return;
            }
            if (((UserOrBot) sender).getCode().asLong() != 1437100907) {
                return;
            }
    
            final GroupMember groupMember = (GroupMember) sender;
            final String messageCode = event.getMessage().serializeToMessageCode();
            bot.getLogger().info("receive group message: " + messageCode);
    
            final CompoundMessage compoundMessage = CompoundMessageBuilder.newInstance()
                .plus("echoed message: ")
                .plus(event.getMessage())
                .plus(" ")
                .plus(At.of(groupMember.getCode()))
                .build();
    
            final Forward forward = Forward.builder()
                .plus(
                    ForwardElement.newInstance(Code.ofLong(1437100907), "椽子", 0, MessageCode.deserializeMessageCode("芜湖！"))
                )
                .plus(
                    ForwardElement.newInstance(Code.ofLong(1437100907), "芜湖", 1, compoundMessage)
                ).build();
            
            event.getTarget().sendMessage(forward);
        });
    
        final net.mamoe.mirai.Bot qqBot = ((QqBot) bot).getQqBot();
        qqBot.getEventChannel().registerListenerHost(new ListenerHost() {
            @EventHandler
            void handleGroupMsg(GroupMessageEvent event) {
                if (event.getSender().getId() != 1437100907) {
                    return;
                }
                final String content = event.getMessage().contentToString();
                System.out.println(event.getMessage());
    
                final String jsonPrefix = "app:json:";
                if (content.startsWith(jsonPrefix)) {
                    final String substring = content.substring(jsonPrefix.length());
                    event.getGroup().sendMessage(new LightApp(substring));
                    event.getGroup().sendMessage(new SimpleServiceMessage(1, substring));
                }
                final String xmlPrefix = "app:xml:";
                if (content.startsWith(xmlPrefix)) {
                    event.getGroup().sendMessage(new SimpleServiceMessage(60, content.substring(xmlPrefix.length())));
                }
            }
        });

//        bot.getEventManager().registerListeners(new Object() {
//
//            @EventHandler
//            void handleGroupMessageEvent(ReceiveGroupMessageEvent event) {
//                final CompoundMessage message = event.getMessage();
//                final GroupSender sender = event.getSender();
//
//                if (sender instanceof UserOrBot && ((UserOrBot) sender).getCode().asLong() != 1437100907) {
//                    return;
//                }
//
//
////                final MessageSource reference = message.getMetadataOrFail(MessageMetadataType.SOURCE);
////                final CompoundMessage compoundMessage = Quote.of(reference)
////                    .plus(message)
////                    .asBuilder()
//////                    .plus(At.singleton(groupSender.getCode()))
////                    .build();
////
////                event.getTarget().sendMessage(compoundMessage);
//            }
//
//            @EventHandler
//            void handleGroupMessageRecallEvent(GroupMessageRecallEvent event) {
//                final CompoundMessage message = event.getMessage();
//                final GroupMember sender = (GroupMember) event.getSender();
//
//                if (sender.getCode().asLong() != 1437100907) {
//                    return;
//                }
//
//                final Group group = event.getTarget();
//                final CompoundMessageBuilder messageBuilder = CompoundMessageBuilder.newInstance()
//                    .plus(At.singleton(sender.getCode()))
//                    .plus("你刚才是不是撤回了一条消息");
//
//                if (Objects.isNull(message)) {
//                    messageBuilder.plus("，但是消息找不到了。");
//                } else {
//                    messageBuilder.plus("：")
//                        .plus(message);
//                }
//
//                group.sendMessage(messageBuilder);
//            }
//        });
    
        TimeUnit.DAYS.sleep(1);
    }
}
