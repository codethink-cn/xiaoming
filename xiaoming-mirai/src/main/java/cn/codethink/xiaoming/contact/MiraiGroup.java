package cn.codethink.xiaoming.contact;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.AbstractBot;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.annotation.Internal;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.concurrent.BotFuture;
import cn.codethink.xiaoming.concurrent.Scheduler;
import cn.codethink.xiaoming.exception.NoSuchGroupException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MiraiMessage;
import cn.codethink.xiaoming.message.MiraiMessageChain;
import cn.codethink.xiaoming.message.content.MessageContent;
import lombok.Data;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * mirai 的 Friend
 *
 * @author Chuanwise
 */
@Internal
@Data
@SuppressWarnings("all")
public class MiraiGroup
        extends AbstractGroup {
    
    /**
     * Mirai 群
     */
    private final Group miraiGroup;
    
    /**
     * 群号
     */
    private final LongCode code;
    
    private final MiraiGroupConfiguration configuration;
    
    public MiraiGroup(MiraiBot bot, Group miraiGroup) {
        super(bot);
        Preconditions.namedArgumentNonNull(miraiGroup, "group");
        
        this.miraiGroup = miraiGroup;
        
        code = LongCode.valueOf(miraiGroup.getId());
        
        configuration = new MiraiGroupConfiguration(this);
    }
    
    @Override
    public boolean isBotInGroup() {
        final MiraiBot miraiBot = (MiraiBot) bot;
        return Objects.nonNull(miraiBot.getMiraiBot().getGroup(code.getCode()));
    }
    
    @Override
    public BotFuture<Message> sendMessage(MessageContent messageContent) {
        Preconditions.namedArgumentNonNull(messageContent, "message content");
    
        final MiraiBot bot = (MiraiBot) this.bot;
        final Scheduler scheduler = bot.getScheduler();
        
        @SuppressWarnings("all")
        final BotFuture<Message> future = (BotFuture) scheduler.submit(() -> {
            final MessageChain messageChain = MiraiMessageChain.serialize(messageContent);
            final MessageReceipt<Group> receipt = miraiGroup.sendMessage(messageChain);
        
            return new MiraiMessage(bot.getResourcePool().allocateMessageCode(), messageContent, receipt.getSource().getTime(), messageChain);
        });
    
        return future;
    }
    
    @Override
    public List<Member> getMembers() {
        return Collections.unmodifiableList(
            miraiGroup.getMembers()
                .stream()
                .map(member -> new MiraiMember((MiraiBot) bot, MiraiGroup.this, member))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Member getMember(Code code) {
        Preconditions.namedArgumentNonNull(code, "code");
        Preconditions.argument(code instanceof LongCode, "code should be long code");
    
        if (!isBotInGroup()) {
            throw new NoSuchGroupException(bot, code);
        }
    
        final long memberCode = ((LongCode) code).getCode();
        final NormalMember member = miraiGroup.get(memberCode);
    
        if (Objects.isNull(member)) {
            return new MiraiMember((MiraiBot) bot, this, member);
        }
        
        return null;
    }
    
    @Override
    public Member getBotSelf() {
        return new MiraiMember((MiraiBot) bot, this, miraiGroup.getBotAsMember());
    }
    
    @Override
    public String getName() {
        assertBotIsInGroup();
        return miraiGroup.getName();
    }
    
    @Override
    public String getAvatarUrl() {
        assertBotIsInGroup();
        return miraiGroup.getAvatarUrl();
    }
    
    @Override
    public Member getOwner() {
        assertBotIsInGroup();
        return new MiraiMember((MiraiBot) bot, this, miraiGroup.getOwner());
    }
    
    @Override
    public GroupConfiguration getConfiguration() {
        return configuration;
    }
    
    protected void assertBotIsInGroup() {
        if (!isBotInGroup()) {
            throw new NoSuchGroupException(bot, code);
        }
    }
}
