package cn.codethink.xiaoming.contact;

import cn.chuanwise.common.util.StaticUtilities;
import cn.codethink.xiaoming.message.metadata.MessageSourceType;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.MessageSourceKind;
import net.mamoe.mirai.message.data.OfflineMessageSource;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.NoSuchElementException;

/**
 * mirai 消息源类型
 *
 * @author Chuanwise
 */
public class MiraiMessageSourceType
    extends StaticUtilities {
    
    /**
     * 通过多态性，转换 mirai 消息源类型为小明消息源类型
     *
     * @param messageSource mirai 消息源
     * @return 小明消息源类型
     */
    public static MessageSourceType fromMirai(MessageSource messageSource) {
        final MessageSourceType messageSourceType;
        if (messageSource instanceof OfflineMessageSource) {
            messageSourceType = MiraiMessageSourceType.fromMirai(((OfflineMessageSource) messageSource).getKind());
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromFriend
            || messageSource instanceof OnlineMessageSource.Outgoing.ToFriend) {
            messageSourceType = MessageSourceType.FRIEND;
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromGroup
            || messageSource instanceof OnlineMessageSource.Outgoing.ToGroup) {
            messageSourceType = MessageSourceType.GROUP;
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromTemp
            || messageSource instanceof OnlineMessageSource.Outgoing.ToTemp) {
            messageSourceType = MessageSourceType.MEMBER;
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromStranger
            || messageSource instanceof OnlineMessageSource.Outgoing.ToStranger) {
            messageSourceType = MessageSourceType.STRANGER;
        } else {
            throw new NoSuchElementException();
        }
        return messageSourceType;
    }
    
    /**
     * 转换 mirai 消息源类型为小明消息源类型
     *
     * @param kind mirai 消息源类型
     * @return 小明消息源类型
     */
    public static MessageSourceType fromMirai(MessageSourceKind kind) {
        switch (kind) {
            case TEMP:
                return MessageSourceType.MEMBER;
            case GROUP:
                return MessageSourceType.GROUP;
            case FRIEND:
                return MessageSourceType.FRIEND;
            case STRANGER:
                return MessageSourceType.STRANGER;
            default:
                throw new NoSuchElementException();
        }
    }
    
    /**
     * 转换小明消息源类型为 mirai 消息源类型
     *
     * @param type 小明消息源类型
     * @return mirai 消息源类型
     */
    public static MessageSourceKind toMirai(MessageSourceType type) {
        switch (type) {
            case GROUP:
                return MessageSourceKind.GROUP;
            case FRIEND:
                return MessageSourceKind.FRIEND;
            case MEMBER:
                return MessageSourceKind.TEMP;
            case STRANGER:
                return MessageSourceKind.STRANGER;
            default:
                throw new NoSuchElementException();
        }
    }
}