package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.concurrent.BotFuture;
import cn.codethink.xiaoming.concurrent.Scheduler;
import cn.codethink.xiaoming.exception.NoSuchGroupException;
import cn.codethink.xiaoming.exception.NoSuchMemberException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MiraiMessage;
import cn.codethink.xiaoming.message.MiraiMessageChain;
import cn.codethink.xiaoming.message.ResourcePool;
import cn.codethink.xiaoming.message.content.MessageContent;
import lombok.Data;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Objects;

/**
 * mirai 的 Friend
 *
 * @author Chuanwise
 */
@InternalAPI
@Data
@SuppressWarnings("all")
public class MiraiMember
        extends AbstractMember {
    
    private final Member member;
    
    private final MiraiGroup group;
    
    private final LongCode code;
    
    public MiraiMember(MiraiBot bot, MiraiGroup group, Member member) {
        super(bot);
        Preconditions.namedArgumentNonNull(member, "member");
        
        this.member = member;
        this.group = group;
        
        code = LongCode.valueOf(member.getId());
    }
    
    @Override
    public BotFuture<Message> sendMessage(MessageContent messageContent) {
        Preconditions.namedArgumentNonNull(messageContent, "message content");
    
        final MiraiBot bot = (MiraiBot) this.bot;
        final Bot miraiBot = bot.getMiraiBot();
        final Scheduler scheduler = bot.getScheduler();
    
        final MessageChain messageChain = MiraiMessageChain.serialize(messageContent);
    
        // 检查是否是好友
        final Friend friend = miraiBot.getFriend(code.getCode());
        final ResourcePool resourcePool = bot.getResourcePool();
        if (Objects.nonNull(friend)) {
            return scheduler.submit(() -> {
                final MessageReceipt<?> receipt = friend.sendMessage(messageChain);
                
                final Code code = resourcePool.allocateMessageCode();
                final MiraiMessage miraiMessage = new MiraiMessage(code, messageContent, receipt.getSource().getTime(), messageChain);
                
                return resourcePool.cacheMessage(miraiMessage);
            });
        }
    
        // 检查 Bot 是否还在本群
        if (!isBotInGroup()) {
            throw new NoSuchGroupException(bot, code);
        }
        
        // 检查该成员是否还在本群
        final NormalMember member = group.getMiraiGroup().get(code.getCode());
        if (Objects.isNull(member)) {
            // 在群里找
            final ContactList<Group> groups = miraiBot.getGroups();
            for (Group group : groups) {
                // 不重复检查本群
                if (group.getId() == code.getCode()) {
                    continue;
                }
                
                // 检查本群是否存在该成员
                final NormalMember normalMember = group.get(code.getCode());
                if (Objects.isNull(normalMember)) {
                    continue;
                }
    
                // 直接发送消息
                return scheduler.submit(() -> {
                    final MessageReceipt<?> receipt = normalMember.sendMessage(messageChain);
                    
                    final Code code = resourcePool.allocateMessageCode();
                    final MiraiMessage miraiMessage = new MiraiMessage(code, messageContent, receipt.getSource().getTime(), messageChain);
                    
                    return resourcePool.cacheMessage(miraiMessage);
                });
            }
    
            throw new NoSuchMemberException(bot, group, code);
        } else {
            // 直接发送消息
            return scheduler.submit(() -> {
                final MessageReceipt<?> receipt = member.sendMessage(messageChain);
                
                final Code code = resourcePool.allocateMessageCode();
                final MiraiMessage miraiMessage = new MiraiMessage(code, messageContent, receipt.getSource().getTime(), messageChain);
                
                return resourcePool.cacheMessage(miraiMessage);
            });
        }
    }
    
    @Override
    public LongCode getCode() {
        return code;
    }
    
    @Override
    public boolean isFriendNow() {
        final Bot miraiBot = ((MiraiBot) bot).getMiraiBot();
        return Objects.nonNull(miraiBot.getFriend(code.getCode()));
    }
    
    @Override
    public boolean isMemberNow() {
        if (!isBotInGroup()) {
            throw new NoSuchGroupException(bot, code);
        }
        return Objects.nonNull(group.getMiraiGroup().get(code.getCode()));
    }
    
    public boolean isBotInGroup() {
        return group.isBotInGroup();
    }
    
    public MiraiGroup getGroup() {
        return group;
    }
    
    @Override
    public Scope getScope() {
        return group;
    }
}
