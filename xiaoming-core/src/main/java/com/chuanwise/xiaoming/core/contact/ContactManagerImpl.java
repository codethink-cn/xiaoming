package com.chuanwise.xiaoming.core.contact;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.ContactManager;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.core.contact.contact.*;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class ContactManagerImpl extends ModuleObjectImpl implements ContactManager {
    final Map<Long, PrivateContact> privateContacts = new HashMap<>();

    final Map<Long, Map<Long, MemberContact>> memberContacts = new HashMap<>();

    final Map<Long, GroupContact> groupContacts = new HashMap<>();

    final Map<String, List<GroupMessage>> groupRecentMessages = new HashMap<>();

    @Override
    public void clear() {
        privateContacts.clear();
        memberContacts.clear();
        groupContacts.clear();
        groupRecentMessages.clear();
    }

    public ContactManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public PrivateContact getBotPrivateContact() {
        final Bot miraiBot = getXiaomingBot().getMiraiBot();

        final long qq = miraiBot.getId();
        PrivateContact privateContact = privateContacts.get(qq);
        Friend friend = miraiBot.getAsFriend();

        // 没有记录过，就创建新的记录
        if (Objects.isNull(privateContact) && Objects.nonNull(friend)) {
            privateContact = new PrivateContactImpl(getXiaomingBot(), friend);
            privateContacts.put(qq, privateContact);
        }

        // 记录过但是本次 get 不到，说明人没了
        if (Objects.isNull(friend) && Objects.nonNull(privateContact)) {
            privateContacts.remove(qq);
            privateContact = null;
        }

        return privateContact;
    }

    @Override
    public PrivateContact getPrivateContact(long qq) {
        PrivateContact privateContact = privateContacts.get(qq);
        Friend friend = getXiaomingBot().getMiraiBot().getFriend(qq);

        // 没有记录过，就创建新的记录
        if (Objects.isNull(privateContact) && Objects.nonNull(friend)) {
            privateContact = new PrivateContactImpl(getXiaomingBot(), friend);
            privateContacts.put(qq, privateContact);
        }

        // 记录过但是本次 get 不到，说明人没了
        if (Objects.isNull(friend) && Objects.nonNull(privateContact)) {
            privateContacts.remove(qq);
            privateContact = null;
        }

        return privateContact;
    }

    @Override
    public GroupContact getGroupContact(long code) {
        GroupContact groupContact = groupContacts.get(code);
        Group group = getXiaomingBot().getMiraiBot().getGroup(code);

        // 没有记录过，就创建新的记录
        if (Objects.isNull(groupContact) && Objects.nonNull(group)) {
            groupContact = new GroupContactImpl(getXiaomingBot(), group);
            groupContacts.put(code, groupContact);
        }

        // 记录过但是本次 get 不到，说明人没了
        if (Objects.isNull(group) && Objects.nonNull(groupContact)) {
            groupContacts.remove(code);
            groupContact = null;
        }

        return groupContact;
    }

    @Override
    public MemberContact getMemberContact(long code, long qq) {
        final Group miraiGroup = getXiaomingBot().getMiraiBot().getGroup(code);
        Map<Long, MemberContact> contactMap = memberContacts.get(code);

        // 在这个群，但还没记录这个群
        if (Objects.isNull(contactMap) && Objects.nonNull(miraiGroup)) {
            contactMap = new HashMap<>();
            memberContacts.put(code, contactMap);
        }

        // 记录了这个群，但发现现在找不到了，说明该退了
        if (Objects.isNull(miraiGroup) && Objects.nonNull(contactMap)) {
            memberContacts.remove(code);
            contactMap = null;
        }

        // 如果没这个群了，就退
        if (Objects.isNull(contactMap)) {
            return null;
        }

        MemberContact memberContact = contactMap.get(qq);
        NormalMember miraiMember = miraiGroup.get(qq);

        // 记录了本群，但还没记录这个成员
        if (Objects.isNull(memberContact) && Objects.nonNull(miraiMember)) {
            memberContact = new MemberContactImpl(getGroupContact(code), miraiMember);
            contactMap.put(qq, memberContact);
        }

        // 记录了本群，但这个成员无了
        if (Objects.isNull(miraiMember) && Objects.nonNull(memberContact)) {
            contactMap.remove(qq);
            memberContact = null;
        }

        if (contactMap.isEmpty()) {
            memberContacts.remove(code);
        }

        return memberContact;
    }
}
