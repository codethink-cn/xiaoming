package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Getter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

@Getter
public class GroupContactImpl extends XiaomingContactImpl<Group> implements GroupContact {
    public GroupContactImpl(XiaomingBot xiaomingBot, Group miraiContact) {
        super(xiaomingBot, miraiContact);
    }
}