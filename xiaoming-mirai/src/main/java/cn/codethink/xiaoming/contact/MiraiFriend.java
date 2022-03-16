package cn.codethink.xiaoming.contact;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.annotation.Internal;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.concurrent.BotFuture;
import cn.codethink.xiaoming.concurrent.Scheduler;
import cn.codethink.xiaoming.concurrent.SucceedBotFuture;
import cn.codethink.xiaoming.exception.NoSuchFriendException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MiraiMessage;
import cn.codethink.xiaoming.message.MiraiMessageChain;
import cn.codethink.xiaoming.message.content.MessageContent;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Objects;

/**
 * mirai 的 Friend
 *
 * @author Chuanwise
 */
@Internal
public class MiraiFriend
        extends AbstractFriend {
    
    private final Friend miraiFriend;
    
    private final LongCode code;
    
    public MiraiFriend(MiraiBot bot, Friend miraiFriend) {
        super(bot);
        Preconditions.namedArgumentNonNull(miraiFriend, "mirai friend");
        
        this.miraiFriend = miraiFriend;
        
        code = LongCode.valueOf(miraiFriend.getId());
    }
    
    @Override
    public boolean isFriendNow() {
        final Bot miraiBot = ((MiraiBot) bot).getMiraiBot();
        return Objects.nonNull(miraiBot.getFriend(code.getCode()));
    }
    
    protected void assertIsFriend() {
        if (!isFriendNow()) {
            throw new NoSuchFriendException(bot, code);
        }
    }
    
    @Override
    public String getAvatarUrl() {
        assertIsFriend();
        return miraiFriend.getAvatarUrl();
    }
    
    @Override
    public BotFuture<Message> sendMessage(MessageContent messageContent) {
        Preconditions.namedArgumentNonNull(messageContent, "message content");
    
        final MiraiBot bot = (MiraiBot) this.bot;
        final Scheduler scheduler = bot.getScheduler();
        
        // 检查这个好友现在还是不是好友
        final Bot miraiBot = bot.getMiraiBot();
    
        final MessageChain messageChain = MiraiMessageChain.serialize(messageContent);
        if (isFriendNow()) {
            return scheduler.submit(() -> {
                final MessageReceipt<Friend> receipt = this.miraiFriend.sendMessage(messageChain);
                return new MiraiMessage(bot.getResourcePool().allocateMessageCode(), messageContent, receipt.getSource().getTime(), messageChain);
            });
        } else {
            // 如果不是好友，则尝试直接发送，然后立即封号
            // 在所有群里尝试发起临时会话
    
            // TODO: 2022/3/16 当群聊禁止临时会话时发送，会被封号
            final ContactList<Group> groups = miraiBot.getGroups();
    
            for (Group group : groups) {
                final NormalMember member = group.get(code.getCode());
        
                if (Objects.isNull(member)) {
                    continue;
                }
        
                final MessageReceipt<Friend> receipt = this.miraiFriend.sendMessage(messageChain);
                final MiraiMessage message = new MiraiMessage(bot.getResourcePool().allocateMessageCode(), messageContent, receipt.getSource().getTime(), messageChain);
        
                return new SucceedBotFuture<>(bot, message);
            }
    
            throw new NoSuchFriendException(bot, code);
        }
    }
    
    @Override
    public Code getCode() {
        return code;
    }
}
