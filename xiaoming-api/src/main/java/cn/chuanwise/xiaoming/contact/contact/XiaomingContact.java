package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.api.OriginalTagMarkable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.TagUtil;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.message.MessageSendable;
import cn.chuanwise.xiaoming.object.XiaomingObject;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.ExternalResource;

public interface XiaomingContact<C extends Contact>
        extends XiaomingObject, OriginalTagMarkable, MessageSendable<Optional<Message>> {
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

    @Override
    Optional<Message> sendMessage(MessageChain messages);

    Optional<Message> nextMessage(long timeout) throws InterruptedException;

    default Optional<Message> nextMessage() throws InterruptedException {
        return nextMessage(getXiaomingBot().getConfiguration().getMaxUserInputTimeout());
    }

    default Image uploadImage(ExternalResource resource) {
        return getMiraiContact().uploadImage(resource);
    }

    @Override
    default Set<String> getOriginalTags() {
        return CollectionUtil.asSet(getCodeString(), TagUtil.ALL);
    }

    @Override
    default String format(String format, Object... contexts) {
        final LanguageManager languageManager = getXiaomingBot().getLanguageManager();

        // 替换 Language 中的字句
        return languageManager.formatAdditional(format, variable -> {
            if (Objects.equals(variable, "contact")) {
                return XiaomingContact.this;
            } else {
                return null;
            }
        }, contexts);
    }

    @Override
    default String format(Sentence sentence, Object... arguments) {
        final LanguageManager languageManager = getXiaomingBot().getLanguageManager();
        return languageManager.formatAdditional(sentence, variable -> {
            if (Objects.equals(variable, "contact")) {
                return XiaomingContact.this;
            } else {
                return null;
            }
        }, arguments);
    }
}
