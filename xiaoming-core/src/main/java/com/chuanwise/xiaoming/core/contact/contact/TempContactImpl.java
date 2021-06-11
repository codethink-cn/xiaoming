package com.chuanwise.xiaoming.core.contact.contact;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.core.contact.message.TempMessageImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 表示群中的成员。不一定是小明用户
 * @author Chuanwise
 */
@Getter
public class TempContactImpl extends XiaomingContactImpl<TempMessage, NormalMember> implements TempContact {
    final NormalMember miraiContact;
    final GroupContact groupContact;
    final List<TempMessage> recentMessages = new LinkedList<>();

    public TempContactImpl(GroupContact groupContact, NormalMember miraiContact) {
        super(groupContact.getXiaomingBot());
        this.miraiContact = miraiContact;
        this.groupContact = groupContact;
    }

    @Override
    public NormalMember getMiraiContact() {
        return miraiContact;
    }

    @Override
    public void mute(long timeMillis) {
        miraiContact.mute((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis));
    }

    @Override
    public TempMessage send(MessageChain messages) {
        return new TempMessageImpl(getXiaomingBot().getReceptionistManager().getBotReceptionist().getOrPutTempXiaomingUser(this),
                miraiContact.sendMessage(messages).getSource().getOriginalMessage());
    }
}