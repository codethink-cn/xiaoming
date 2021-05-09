package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.util.ArgumentUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;

/**
 * 群内的小明使用者
 * @author Chuanwise
 */
@Data
public class GroupXiaomingUserImpl extends XiaomingUserImpl implements GroupXiaomingUser {
    Member asGroupMember;
    StringBuilder buffer = new StringBuilder();
    boolean useBuffer = false;

    public GroupXiaomingUserImpl(XiaomingBot xiaomingBot, Member member) {
        setXiaomingBot(xiaomingBot);
        setAsGroupMember(member);
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
            return sendGroupMessage(new At(getAsGroupMember().getId()).serializeToMiraiCode() + " " + messageWithoutArguments);
        }
    }

    @Override
    public Friend getAsFriend() {
        return getXiaomingBot().getMiraiBot().getFriend(getAsGroupMember().getId());
    }

    @Override
    public boolean sendGroupMessage(String message, Object... arguments) {
        asGroupMember.getGroup().sendMessage(MiraiCode.deserializeMiraiCode(ArgumentUtil.replaceArguments(message, arguments)));
        return true;
    }

    @Override
    public boolean sendPrivateMessage(String message, Object... arguments) {
        asGroupMember.sendMessage(MiraiCode.deserializeMiraiCode(ArgumentUtil.replaceArguments(message, arguments)));
        return true;
    }

    @Override
    public long getQQ() {
        return asGroupMember.getId();
    }

    @Override
    public long getGroupNumber() {
        return asGroupMember.getGroup().getId();
    }
}