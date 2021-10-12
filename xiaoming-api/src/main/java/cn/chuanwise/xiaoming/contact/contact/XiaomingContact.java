package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.toolkit.tag.TagMarkable;

import java.util.Optional;
import java.util.Set;

import cn.chuanwise.xiaoming.util.MiraiCodeUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.ExternalResource;

public interface XiaomingContact<C extends Contact> extends XiaomingObject, TagMarkable {
    String getAliasAndCode();

    C getMiraiContact();

    default long getCode() {
        return getMiraiContact().getId();
    }

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    String getName();

    String getAlias();

    String getAvatarUrl();

    default Message sendMessage(String message) {
        final String replacedMessage = getXiaomingBot().getLanguageManager().format(message);
        return sendMessage(MiraiCode.deserializeMiraiCode(replacedMessage));
    }

    Message sendMessage(MessageChain messages);

    default Message sendMessage(Message messages) {
        messages.setOriginalMessageChain(getMiraiContact().sendMessage(messages.getMessageChain()).getSource().getOriginalMessage());
        return messages;
    }

    default Message sendMessage(SingleMessage... messages) {
        return sendMessage(MiraiCodeUtil.asMessageChain(messages));
    }


    default Message reply(Message quote, MessageChain messages) {
        return sendMessage(new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(messages));
    }

    default Message reply(Message quote, Message message) {
        return reply(quote, message.getMessageChain());
    }

    default Message reply(Message quote, String message) {
        return reply(quote, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().format(message)));
    }


    Optional<Message> nextMessage(long timeout) throws InterruptedException;

    default Optional<Message> nextMessage() throws InterruptedException {
        return nextMessage(getXiaomingBot().getConfiguration().getMaxUserInputTimeout());
    }

    default Image uploadImage(ExternalResource resource) {
        return getMiraiContact().uploadImage(resource);
    }


    @Override
    default Set<String> getOriginalTags() {
        return CollectionUtil.asSet(getCodeString(), RECORDED);
    }
}
