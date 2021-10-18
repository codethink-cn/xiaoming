package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.ContactManager;
import lombok.Getter;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GroupContactImpl extends XiaomingContactImpl<Group> implements GroupContact {
    public GroupContactImpl(XiaomingBot xiaomingBot, Group miraiContact) {
        super(xiaomingBot, miraiContact);
    }

    @Override
    public MemberContact getBotMember() {
        return new MemberContactImpl(this, miraiContact.getBotAsMember());
    }

    @Override
    public MemberContact getOwner() {
        return new MemberContactImpl(this, getMiraiContact().getOwner());
    }

    @Override
    public List<MemberContact> getMembers() {
        return getMiraiContact().getMembers().stream()
                .map(member -> new MemberContactImpl(this, member))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void flush() {
        getGroupInformation().flush();
    }

    @Override
    public boolean addTag(String tag) {
        return getGroupInformation().addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return getGroupInformation().hasTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return getGroupInformation().removeTag(tag);
    }
}