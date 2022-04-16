package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.Preconditions;
import cn.chuanwise.common.util.StaticUtilities;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.basic.MessageMetadata;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.basic.AtAll;
import cn.codethink.xiaoming.message.metadata.*;
import cn.codethink.xiaoming.util.MiraiConvertors;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.code.CodableMessage;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Dice;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.FlashImage;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MusicShare;
import net.mamoe.mirai.message.data.VipFace;
import net.mamoe.mirai.utils.ExternalResource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuanwise
 */
public class MiraiMessageChain
    extends StaticUtilities {
    
    /**
     * 将消息引用转换为 mirai 消息源
     *
     * @param messageReference 消息引用
     * @return mirai 消息源
     * @throws NullPointerException messageReference 为 null
     * @throws NullPointerException 转换消息链可能涉及上传资源，如 {@link net.mamoe.mirai.message.data.Image} 需要由 mirai 会话上传
     *                              {@link Contact#uploadImage(ExternalResource)}。函数将不会检查
     *                              contact 是否为 null。如果需要上传类似的资源但 contact 为 null，将会得到一个异常。
     */
    public static MessageSource fromMessageReference(MessageReference messageReference, Contact contact) {
        Preconditions.objectNonNull(messageReference, "message source");
    
        // if it's mirai reference, use it
        if (messageReference instanceof AbstractMiraiOnlineMessageReference) {
            return ((AbstractMiraiOnlineMessageReference) messageReference).getMiraiMessageSource();
        }
        if (messageReference instanceof MiraiOfflineMessageReference) {
            return ((MiraiOfflineMessageReference) messageReference).getMiraiMessageSource();
        }
        
        // build an offline message source
        final MessageSourceBuilder messageSourceBuilder = new MessageSourceBuilder()
            .messages(fromCompoundMessage(messageReference.getMessage().asCompoundMessage(), contact))
            .time((int) TimeUnit.MILLISECONDS.toSeconds(messageReference.getTimestamp()));
        
        messageSourceBuilder.setFromId(messageReference.getSourceCode().asLong());
        messageSourceBuilder.setTargetId(messageReference.getTargetCode().asLong());
    
        return messageSourceBuilder
            .build(messageReference.getBotCode().asLong(), MiraiMessageSourceType.toMirai(messageReference.getMessageSourceType()));
    }
    
    /**
     * 将 mirai 消息源转化为小明消息引用。
     *
     * @param messageSource mirai 消息源
     * @param bot           bot
     * @return 小明消息引用
     * @throws NullPointerException messageSource 或 bot 为 null
     */
    public static MessageReference toMessageReference(MessageSource messageSource, MiraiBot bot) {
        Preconditions.objectNonNull(messageSource, "message source");
        Preconditions.objectNonNull(bot, "bot");
    
        if (messageSource instanceof OfflineMessageSource) {
            final OfflineMessageSource offlineMessageSource = (OfflineMessageSource) messageSource;
            return toOfflineMessageReference(offlineMessageSource, bot);
        }
        if (messageSource instanceof OnlineMessageSource) {
            final OnlineMessageSource onlineMessageSource = (OnlineMessageSource) messageSource;
            return toOnlineMessageReference(onlineMessageSource, bot);
        }
    
        throw new NoSuchElementException("can not convert the message source: " + messageSource);
    }
    
    /**
     * 将 mirai 离线消息源转化为小明的离线消息引用。
     *
     * @param offlineMessageSource mirai 离线消息源
     * @param bot                  bot
     * @return 小明离线消息引用
     * @throws NullPointerException offlineMessageSource 或 bot 为 null
     */
    public static OfflineMessageReference toOfflineMessageReference(OfflineMessageSource offlineMessageSource, MiraiBot bot) {
        Preconditions.objectNonNull(offlineMessageSource, "offline message source");
        Preconditions.objectNonNull(bot, "bot");
    
        return new MiraiOfflineMessageReference(offlineMessageSource);
    }
    
    /**
     * 将 mirai 在线消息源转化为小明的在线消息引用。
     *
     * @param onlineMessageSource mirai 在线消息源
     * @param bot                 bot
     * @return 小明在线消息引用
     * @throws NullPointerException onlineMessageSource 或 bot 为 null
     */
    public static OnlineMessageReference toOnlineMessageReference(OnlineMessageSource onlineMessageSource, MiraiBot bot) {
        Preconditions.objectNonNull(onlineMessageSource, "online message source");
        Preconditions.objectNonNull(bot, "bot");
    
        if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromFriend) {
            final OnlineMessageSource.Incoming.FromFriend messageSource = (OnlineMessageSource.Incoming.FromFriend) onlineMessageSource;
            
            final MiraiFriend friend = bot.getFriendOrFail(Code.ofLong(messageSource.getFromId()));
            return new MiraiFromFriendMessageReference(messageSource, friend);
        }
        
        if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromGroup) {
            final OnlineMessageSource.Incoming.FromGroup messageSource = (OnlineMessageSource.Incoming.FromGroup) onlineMessageSource;
    
            final MiraiMember member = bot
                .getGroupOrFail(Code.ofLong(messageSource.getGroup().getId()))
                .getMemberOrFail(Code.ofLong(messageSource.getSender().getId()));
    
            return new MiraiFromGroupMessageReference(messageSource, member);
        }
        
        if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromStranger) {
            final OnlineMessageSource.Incoming.FromStranger messageSource = (OnlineMessageSource.Incoming.FromStranger) onlineMessageSource;
    
            final MiraiStranger stranger = bot.getStrangerOrFail(Code.ofLong(messageSource.getFromId()));
            return new MiraiFromStrangerMessageReference(messageSource, stranger);
        }
    
        if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromTemp) {
            final OnlineMessageSource.Incoming.FromTemp messageSource = (OnlineMessageSource.Incoming.FromTemp) onlineMessageSource;
        
            final MiraiMember member = bot
                .getGroupOrFail(Code.ofLong(messageSource.getGroup().getId()))
                .getMemberOrFail(Code.ofLong(messageSource.getSender().getId()));
        
            return new MiraiFromGroupMemberMessageReference(messageSource, member);
        }
        
        if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToFriend) {
            final OnlineMessageSource.Outgoing.ToFriend messageSource = (OnlineMessageSource.Outgoing.ToFriend) onlineMessageSource;
    
            final MiraiFriend friend = bot.getFriendOrFail(Code.ofLong(messageSource.getTargetId()));
            return new MiraiToFriendMessageReference(messageSource, friend);
        }
        
        if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToGroup) {
            final OnlineMessageSource.Outgoing.ToGroup messageSource = (OnlineMessageSource.Outgoing.ToGroup) onlineMessageSource;
    
            final MiraiGroup group = bot.getGroupOrFail(Code.ofLong(messageSource.getTargetId()));
            return new MiraiToGroupMessageReference(messageSource, group);
        }
    
        if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToTemp) {
            final OnlineMessageSource.Outgoing.ToTemp messageSource = (OnlineMessageSource.Outgoing.ToTemp) onlineMessageSource;
        
            final MiraiMember member = bot.getGroupOrFail(Code.ofLong(messageSource.getGroup().getId()))
                .getMemberOrFail(Code.ofLong(messageSource.getTargetId()));
            
            return new MiraiToGroupMemberMessageReference(messageSource, member);
        }
        
        if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToStranger) {
            final OnlineMessageSource.Outgoing.ToStranger messageSource = (OnlineMessageSource.Outgoing.ToStranger) onlineMessageSource;
    
            final MiraiStranger stranger = bot.getStrangerOrFail(Code.ofLong(messageSource.getTargetId()));
            return new MiraiToStrangerMessageReference(messageSource, stranger);
        }
        
        throw new NoSuchElementException("can not convert the online message source: " + onlineMessageSource);
    }
    
    /**
     * 将消息转换为 mirai 消息
     *
     * @param message 消息
     * @param contact 会话
     * @return mirai 消息
     * @throws NullPointerException message 为 null
     * @throws NullPointerException 转换消息链可能涉及上传资源，如 {@link net.mamoe.mirai.message.data.Image} 需要由 mirai 会话上传
     *                              {@link Contact#uploadImage(ExternalResource)}。函数将不会检查
     *                              contact 是否为 null。如果需要上传类似的资源但 contact 为 null，将会得到一个异常。
     */
    public static net.mamoe.mirai.message.data.Message fromMessage(Message message, Contact contact) {
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(contact, "contact");
    
        // convert message
        if (message instanceof CompoundMessage) {
            final CompoundMessage compoundMessage = (CompoundMessage) message;
            return fromCompoundMessage(compoundMessage, contact);
        }
        if (message instanceof BasicMessage) {
            final BasicMessage basicMessage = (BasicMessage) message;
            return fromBasicMessage(basicMessage, contact);
        }
    
        if (message instanceof Serializable) {
            throw new NoSuchElementException("can not convert message: " + ((Serializable) message).serializeToMessageCode() + " to mirai single message");
        } else {
            throw new NoSuchElementException("can not convert message: " + message + " to mirai single message");
        }
    }
    
    /**
     * 将复合消息转为 mirai 消息链。
     *
     * @param compoundMessage 复合消息
     * @param contact         会话
     * @return mirai 消息链
     * @throws NullPointerException compoundMessage 为 null
     * @throws NullPointerException 转换消息链可能涉及上传资源，如 {@link net.mamoe.mirai.message.data.Image} 需要由 mirai 会话上传
     *                              {@link Contact#uploadImage(ExternalResource)}。函数将不会检查
     *                              contact 是否为 null。如果需要上传类似的资源但 contact 为 null，将会得到一个异常。
     */
    public static MessageChain fromCompoundMessage(CompoundMessage compoundMessage, Contact contact) {
    
        final Map<MessageMetadataType<? extends MessageMetadata>, MessageMetadata> metadata = compoundMessage.getMetadata();
        final List<SingleMessage> singleMessages = new ArrayList<>(compoundMessage.size() + metadata.size());
    
        // metadata
        for (Map.Entry<MessageMetadataType<? extends MessageMetadata>, MessageMetadata> entry : metadata.entrySet()) {
            final MessageMetadataType<?> messageMetadataType = entry.getKey();
            
            // convert reference
            if (Objects.equals(messageMetadataType, MessageMetadataType.QUOTE)) {
                final Quote quote = (Quote) entry.getValue();
                final MessageReference messageReference = quote.getMessageReference();
                singleMessages.add(fromMessageReference(messageReference, contact));
                continue;
            }
    
            // convert reference
            if (Objects.equals(messageMetadataType, MessageMetadataType.REFERENCE)) {
                final MessageReference messageReference = (MessageReference) entry.getValue();
                singleMessages.add(fromMessageReference(messageReference, contact));
            }
        }
        
        // basic messages
        for (BasicMessage basicMessage : compoundMessage) {
            singleMessages.add((SingleMessage) fromMessage(basicMessage, contact));
        }
    
        // build message chain
        final MessageChainBuilder messageChainBuilder = new MessageChainBuilder(singleMessages.size());
        messageChainBuilder.addAll(singleMessages);
        return messageChainBuilder.asMessageChain();
    }
    
    /**
     * 将 mirai 消息内容转化为小明的基础消息。
     *
     * @param messageContent mirai 消息内容
     * @param contact 相关会话
     * @return 小明的基础消息
     */
    public static BasicMessage toBasicMessage(MessageContent messageContent, Contact contact) {
        Preconditions.objectNonNull(messageContent, "message content");
    
        // TODO: 2022/4/16 check if contact is useful
        
        if (messageContent instanceof PlainText) {
            final PlainText plainText = (PlainText) messageContent;
            return new Text(plainText.getContent());
        }
        
        if (messageContent instanceof At) {
            final At at = (At) messageContent;
            return new AtSingleton(Code.ofLong(at.getTarget()));
        }
    
        if (messageContent instanceof net.mamoe.mirai.message.data.AtAll) {
            return AtAll.INSTANCE;
        }
        
        if (messageContent instanceof Face) {
            final Face face = (Face) messageContent;
            return cn.codethink.xiaoming.message.basic.Face.of(face.getId());
        }
    
        if (messageContent instanceof Dice) {
            final Dice dice = (Dice) messageContent;
            return cn.codethink.xiaoming.message.basic.Dice.of(dice.getValue());
        }
    
        if (messageContent instanceof Image) {
            final Image image = (Image) messageContent;
            
            final String url = Image.queryUrl(image);
            try {
                return new UrlImage(new URL(url));
            } catch (MalformedURLException exception) {
                throw new RuntimeException(exception);
            }
        }
        
        if (messageContent instanceof FlashImage) {
            final FlashImage flashImage = (FlashImage) messageContent;
            
            final String url = Image.queryUrl(flashImage.getImage());
            try {
                return new cn.codethink.xiaoming.message.basic.FlashImage(new UrlImage(new URL(url)));
            } catch (MalformedURLException exception) {
                throw new RuntimeException(exception);
            }
        }
    
        if (messageContent instanceof ForwardMessage) {
            final ForwardMessage forwardMessage = (ForwardMessage) messageContent;
            forwardMessage.getSource();
            // TODO: 2022/4/16 finish forward message
        }
        
        if (messageContent instanceof MusicShare) {
            final MusicShare musicShare = (MusicShare) messageContent;
            return new cn.codethink.xiaoming.message.basic.MusicShare(
                MiraiMusicSoftwareType.fromMirai(musicShare.getKind()),
                musicShare.getTitle(),
                musicShare.getSummary(),
                musicShare.getJumpUrl(),
                musicShare.getPictureUrl(),
                musicShare.getMusicUrl(),
                musicShare.getBrief()
            );
        }
    
        if (messageContent instanceof VipFace) {
            final VipFace vipFace = (VipFace) messageContent;
            return new cn.codethink.xiaoming.message.basic.VipFace(
                MiraiConvertors.fromMirai(vipFace.getKind()),
                vipFace.getCount()
            );
        }
    
        if (messageContent instanceof LightApp) {
            // TODO: 2022/4/16 finish it
        }
    
        if (messageContent instanceof CodableMessage) {
            throw new NoSuchElementException("can not convert message: " + ((CodableMessage) messageContent).serializeToMiraiCode() + " to basic message");
        } else {
            throw new NoSuchElementException("can not convert message: " + messageContent + " to basic message");
        }
    }
    
    public static net.mamoe.mirai.message.data.MessageMetadata fromMetadataMessage(MessageMetadata messageMetadata, Contact contact) {
        Preconditions.objectNonNull(messageMetadata, "metadata message");
    
        if (messageMetadata instanceof Quote) {
            final Quote quote = (Quote) messageMetadata;
            return new QuoteReply(fromMessageReference(quote.getMessageReference(), contact));
        }
        
        if (messageMetadata instanceof MessageReference) {
            final MessageReference messageReference = (MessageReference) messageMetadata;
            return fromMessageReference(messageReference, contact);
        }
    
        throw new NoSuchElementException("can not convert the metadata message source: " + messageMetadata);
    }
    
    public static MessageMetadata toMetadataMessage(net.mamoe.mirai.message.data.MessageMetadata messageMetadata, Contact contact, MiraiBot bot) {
        Preconditions.objectNonNull(messageMetadata, "message metadata");
        Preconditions.objectNonNull(bot, "bot");
    
        if (messageMetadata instanceof QuoteReply) {
            final QuoteReply quoteReply = (QuoteReply) messageMetadata;
            return new Quote(toMessageReference(quoteReply.getSource(), bot));
        }
    
        if (messageMetadata instanceof MessageSource) {
            final MessageSource messageSource = (MessageSource) messageMetadata;
            return toMessageReference(messageSource, bot);
        }
    
        throw new NoSuchElementException("can not convert the metadata message source: " + messageMetadata);
    }
    
    public static SingleMessage fromBasicMessage(BasicMessage basicMessage, Contact contact) {
        Preconditions.objectNonNull(basicMessage, "basic message");
        
        // plain text
        if (basicMessage instanceof Text) {
            final Text text = (Text) basicMessage;
            return new PlainText(text.getText());
        }
    
        // at
        if (basicMessage instanceof AtSingleton) {
            final AtSingleton atSingleton = (AtSingleton) basicMessage;
            return new At(atSingleton.getTargetCode().asLong());
        }
    
        // at all
        if (basicMessage instanceof AtAll) {
            return net.mamoe.mirai.message.data.AtAll.INSTANCE;
        }
    
        // TODO: 2022/4/16 finish basic message types
        throw new NoSuchElementException("can not convert message: " + basicMessage.serializeToMessageCode() + " to mirai single message");
    }
    
    public static CompoundMessage toCompoundMessage(MessageChain messageChain, Contact contact, MiraiBot bot) {
        Preconditions.objectNonNull(messageChain, "message chain");
        
        final CompoundMessageBuilder compoundMessageBuilder = CompoundMessageBuilder.builder();
    
        for (SingleMessage singleMessage : messageChain) {
            if (singleMessage instanceof net.mamoe.mirai.message.data.MessageMetadata) {
                Preconditions.objectNonNull(bot, "bot");
                
                final net.mamoe.mirai.message.data.MessageMetadata messageMetadata = (net.mamoe.mirai.message.data.MessageMetadata) singleMessage;
                final MessageMetadata metadataMessage = toMetadataMessage(messageMetadata, contact, bot);
                compoundMessageBuilder.plus(metadataMessage);
                continue;
            }
            
            if (singleMessage instanceof MessageContent) {
                final MessageContent messageContent = (MessageContent) singleMessage;
                compoundMessageBuilder.plus(toBasicMessage(messageContent, contact));
                continue;
            }
    
            if (singleMessage instanceof CodableMessage) {
                throw new NoSuchElementException("can not convert message: " + ((CodableMessage) singleMessage).serializeToMiraiCode() + " to message");
            } else {
                throw new NoSuchElementException("can not convert message: " + singleMessage + " to message");
            }
        }
        
        return compoundMessageBuilder.build();
    }
}