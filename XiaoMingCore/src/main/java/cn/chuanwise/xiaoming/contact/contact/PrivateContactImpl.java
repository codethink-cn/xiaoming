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

    @Override
    public void flush() {
        getAccount().flush();
    }

    @Override
    public boolean addTag(String tag) {
        return getAccount().addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return getAccount().hasTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return getAccount().removeTag(tag);
    }
}