package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

@Getter
public class PrivateContactImpl extends XiaomingContactImpl<Friend> implements PrivateContact {
    public PrivateContactImpl(XiaomingBot xiaomingBot, Friend miraiContact) {
        super(xiaomingBot, miraiContact);
    }
}