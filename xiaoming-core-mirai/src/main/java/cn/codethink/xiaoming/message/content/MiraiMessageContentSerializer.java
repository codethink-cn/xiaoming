package cn.codethink.xiaoming.message.content;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.*;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.code.IntCode;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.code.StringCode;
import cn.codethink.xiaoming.message.element.*;
import cn.codethink.xiaoming.message.element.AtAll;
import cn.codethink.xiaoming.message.element.FlashImage;
import cn.codethink.xiaoming.message.element.Image;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Dice;
import net.mamoe.mirai.message.data.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Mirai 码转换器
 *
 * @author Chuanwise
 */
public class MiraiMessageContentSerializer
    implements MessageContentSerializer<MessageChain> {
    
    private static final MiraiMessageContentSerializer INSTANCE = new MiraiMessageContentSerializer();
    
    public static MiraiMessageContentSerializer getInstance() {
        return INSTANCE;
    }
    
    private MiraiMessageContentSerializer() {}
    
    @Override
    public MessageContent deserialize(MessageChain messageChain) {
        Preconditions.namedArgumentNonNull(messageChain, "message chain");
    
        final List<MessageElement> messageElements = new ArrayList<>(messageChain.size());
    
        Message replyTarget = null;
    
        for (SingleMessage singleMessage : messageChain) {
            Preconditions.namedArgumentNonNull(singleMessage, "single message");
        
            // plain text
            if (singleMessage instanceof PlainText) {
                final PlainText plainText = (PlainText) singleMessage;
                messageElements.add(new Text(plainText.getContent()));
                continue;
            }
        
            // account at
            if (singleMessage instanceof net.mamoe.mirai.message.data.At) {
                final net.mamoe.mirai.message.data.At at = (net.mamoe.mirai.message.data.At) singleMessage;
                messageElements.add(new AccountAt(LongCode.valueOf(at.getTarget())));
                continue;
            }
        
            // at all
            if (singleMessage instanceof net.mamoe.mirai.message.data.AtAll) {
                messageElements.add(AtAll.getInstance());
                continue;
            }
        
            // image
            if (singleMessage instanceof net.mamoe.mirai.message.data.Image) {
                final net.mamoe.mirai.message.data.Image image = (net.mamoe.mirai.message.data.Image) singleMessage;
                messageElements.add(new cn.codethink.xiaoming.message.element.Image(new StringCode(image.getImageId())));
                continue;
            }
        
            // face
            if (singleMessage instanceof Face) {
                final Face face = (Face) singleMessage;
                messageElements.add(new Expression(IntCode.valueOf(face.getId()), face.getName()));
                continue;
            }
        
            // flash image
            if (singleMessage instanceof net.mamoe.mirai.message.data.FlashImage) {
                final net.mamoe.mirai.message.data.FlashImage flashImage = (net.mamoe.mirai.message.data.FlashImage) singleMessage;
                final cn.codethink.xiaoming.message.element.FlashImage image = new cn.codethink.xiaoming.message.element.FlashImage(new StringCode(flashImage.getImage().getImageId()));
                return new SingletonMessageContent(image);
            }
        
            // reply message
            if (singleMessage instanceof QuoteReply) {
                final QuoteReply quoteReply = (QuoteReply) singleMessage;
                throw new NoSuchElementException();
            }
            
            // dice
            if (singleMessage instanceof Dice) {
                final Dice dice = (Dice) singleMessage;
                messageElements.add(new cn.codethink.xiaoming.message.element.Dice(IntCode.valueOf(dice.getId()), dice.getName(), dice.getValue()));
                continue;
            }
            
            // market expression
            if (singleMessage instanceof MarketFace) {
                final MarketFace marketFace = (MarketFace) singleMessage;
                messageElements.add(new MarketExpression(IntCode.valueOf(marketFace.getId()), marketFace.getName()));
                continue;
            }
        
            throw new NoSuchElementException("no such translator for single message: " + singleMessage.getClass().getSimpleName());
        }
    
        return new SimpleMessageContent(Collections.unmodifiableList(messageElements));
    }
    
    @Override
    public MessageChain serialize(MessageContent content) {
        Preconditions.namedArgumentNonNull(content, "message content");
    
        if (content instanceof SimpleMessageContent) {
            final SimpleMessageContent simpleMessageContent = (SimpleMessageContent) content;
            final MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
    
            for (MessageElement messageElement : simpleMessageContent) {
                // plain text
                if (messageElement instanceof Text) {
                    final Text text = (Text) messageElement;
                    messageChainBuilder.add(new PlainText(text.getText()));
                    continue;
                }
    
                // at
                if (messageElement instanceof AccountAt) {
                    final AccountAt accountAt = (AccountAt) messageElement;
                    final Code code = accountAt.getCode();
        
                    Preconditions.argument(code instanceof LongCode, "code should be long code");
        
                    messageChainBuilder.add(new At(((LongCode) code).getCode()));
                    continue;
                }
    
                // at all
                if (messageElement instanceof AtAll) {
                    messageChainBuilder.add(net.mamoe.mirai.message.data.AtAll.INSTANCE);
                    continue;
                }
                
                // image
                if (messageElement instanceof Image) {
                    final Image image = (Image) messageElement;
                    final Code code = image.getCode();
    
                    Preconditions.argument(code instanceof StringCode, "code should be string code");
                    
                    messageChainBuilder.add(net.mamoe.mirai.message.data.Image.fromId(((StringCode) code).getCode()));
                    continue;
                }
                
                // flash image
                if (messageElement instanceof FlashImage) {
                    final FlashImage flashImage = (FlashImage) messageElement;
                    final Code code = flashImage.getCode();
    
                    Preconditions.argument(code instanceof StringCode, "code should be string code");
                    
                    messageChainBuilder.add(net.mamoe.mirai.message.data.FlashImage.from(((StringCode) code).getCode()));
                    continue;
                }
    
                throw new NoSuchElementException();
            }
            
            return messageChainBuilder.asMessageChain();
        }
        
        throw new NoSuchElementException();
    }
}