package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.StaticUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil extends StaticUtil {
    public static ForwardMessage asBotForwardMessage(XiaomingBot bot,
                                                     String title,
                                                     String brief,
                                                     String source,
                                                     String summary,
                                                     List<MessageChain> messages) {
        final List<ForwardMessage.Node> nodeList = new ArrayList<>(messages.size());

        // build preview
        final int previewSize = Math.min(2, messages.size());
        final List<String> preview = new ArrayList<>(previewSize);
        for (int i = 0; i < previewSize; i++) {
            preview.add(messages.get(i).contentToString());
        }

        // build node
        messages.forEach(message -> {
            final ForwardMessage.Node node =
                    new ForwardMessage.Node(bot.getCode(), (int) System.currentTimeMillis() / 1000, "", message);
            nodeList.add(node);
        });

        return new ForwardMessage(preview, title, brief, source, summary, nodeList);
    }

    public static ForwardMessage asBotForwardMessage(long senderCode,
                                                     String title,
                                                     String brief,
                                                     String source,
                                                     String summary,
                                                     List<Message> messages) {
        final List<ForwardMessage.Node> nodeList = new ArrayList<>(messages.size());

        // build preview
        final int previewSize = Math.min(2, messages.size());
        final List<String> preview = new ArrayList<>(previewSize);
        for (int i = 0; i < previewSize; i++) {
            preview.add(messages.get(i).getMessageChain().contentToString());
        }

        // build node
        messages.forEach(message -> {
            final ForwardMessage.Node node =
                    new ForwardMessage.Node(senderCode, (int) System.currentTimeMillis() / 1000, "", message.getMessageChain());
            nodeList.add(node);
        });

        return new ForwardMessage(preview, title, brief, source, summary, nodeList);
    }
}
