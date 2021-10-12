package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import net.mamoe.mirai.contact.Friend;

import java.util.Optional;
import java.util.Set;

public interface ConsoleContact extends XiaomingContact<Friend> {
    @Override
    default Friend getMiraiContact() {
        return getXiaomingBot().getMiraiBot().getAsFriend();
    }

    @Override
    default String getName() {
        return "后台";
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getAlias() {
        return "后台";
    }

    @Override
    default String getAliasAndCode() {
        return "后台";
    }

    @Override
    default Optional<Message> nextMessage(long timeout) throws InterruptedException {
        return getXiaomingBot().getContactManager()
                .nextMessageEvent(timeout, messageEvent -> messageEvent.getUser() instanceof ConsoleXiaomingUser)
                .map(MessageEvent::getMessage);
    }

    @Override
    default Set<String> getTags() {
        return CollectionUtil.asSet(RECORDED, "console");
    }
}
