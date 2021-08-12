package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.*;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
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

    /** 最近临时会话消息 */
    final Map<String, Map<String, List<MemberMessage>>> memberRecentMessages;

    /** 最近群内每个成员的消息 */
    final Map<String, Map<String, List<GroupMessage>>> groupMemberRecentMessages;

    /** 最近私聊消息 */
    final Map<String, List<PrivateMessage>> privateRecentMessages;

    public ContactManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        this.memberRecentMessages = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxRecentGroupMemberMessageBufferQuantity());
        this.groupMemberRecentMessages = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxRecentGroupMemberMessageBufferQuantity());
        this.privateRecentMessages = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxRecentPrivateMessageBufferQuantity());
    }

    @Override
    public void clear() {
        privateContacts.clear();
        memberContacts.clear();
        groupContacts.clear();
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
    public PrivateContact getPrivateContact(long code) {
        PrivateContact privateContact = privateContacts.get(code);
        Friend friend = getXiaomingBot().getMiraiBot().getFriend(code);

        // 没有记录过，就创建新的记录
        if (Objects.isNull(privateContact) && Objects.nonNull(friend)) {
            privateContact = new PrivateContactImpl(getXiaomingBot(), friend);
            privateContacts.put(code, privateContact);
        }

        // 记录过但是本次 get 不到，说明人没了
        if (Objects.isNull(friend) && Objects.nonNull(privateContact)) {
            privateContacts.remove(code);
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
    public MemberContact getMemberContact(long group, long code) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.isNull(groupContact)) {
            return null;
        }

        final NormalMember member = groupContact.getMiraiContact().get(code);
        return Objects.nonNull(member) ? getMemberContact(groupContact, member) : null;
    }

    @Override
    public MemberContact getMemberContact(GroupContact groupContact, NormalMember miraiMember) {
        final long code = groupContact.getCode();
        Map<Long, MemberContact> memberContacts = this.memberContacts.get(code);
        if (Objects.isNull(memberContacts)) {
            memberContacts = new HashMap<>();
            this.memberContacts.put(code, memberContacts);
        }

        final long qq = miraiMember.getId();
        MemberContact memberContact = memberContacts.get(qq);

        // 记录了本群，但还没记录这个成员
        if (Objects.isNull(memberContact) && Objects.nonNull(miraiMember)) {
            memberContact = new MemberContactImpl(getGroupContact(code), miraiMember);
            memberContacts.put(qq, memberContact);
        }

        // 记录了本群，但这个成员无了
        if (Objects.isNull(miraiMember) && Objects.nonNull(memberContact)) {
            memberContacts.remove(qq);
            memberContact = null;
        }

        if (memberContacts.isEmpty()) {
            this.memberContacts.remove(code);
        }

        return memberContact;
    }
}
