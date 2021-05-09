package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.util.ArgumentUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.code.MiraiCode;

/**
 * 小明的好友使用者
 */
@Data
public class PrivateXiaomingUserImpl extends XiaomingUserImpl implements PrivateXiaomingUser {
    Friend friend;

    public PrivateXiaomingUserImpl(XiaomingBot xiaomingBot, Friend friend) {
        setXiaomingBot(xiaomingBot);
        setFriend(friend);
    }

    @Override
    public boolean sendMessage(String message, Object... arguments) {
        final String messageWithoutArguments = ArgumentUtil.replaceArguments(message, arguments);
        if (isUseBuffer()) {
            final StringBuilder buffer = getBuffer();
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(messageWithoutArguments);
            return true;
        } else {
            return sendPrivateMessage(message, arguments);
        }
    }

    @Override
    public boolean sendPrivateMessage(String message, Object... arguments) {
        friend.sendMessage(MiraiCode.deserializeMiraiCode(ArgumentUtil.replaceArguments(message, arguments)));
        return true;
    }

    @Override
    public long getQQ() {
        return friend.getId();
    }
}