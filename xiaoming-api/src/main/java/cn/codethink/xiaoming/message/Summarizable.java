package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.Maps;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.metadata.MessageMetadataType;
import cn.codethink.xiaoming.message.metadata.MessageSource;
import cn.codethink.xiaoming.message.metadata.OnlineMessageSource;
import cn.codethink.xiaoming.property.Property;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 可以获取摘要的对象
 *
 * @author Chuanwise
 */
public interface Summarizable {
    
    /**
     * 序列化为消息摘要
     *
     * @return 描述消息内容的字符串
     */
    default String serializeToMessageSummary() {
        Bot bot = null;
        Contact contact = null;
    
        if (this instanceof CompoundMessage) {
            final CompoundMessage compoundMessage = (CompoundMessage) this;
        
            final MessageSource reference = compoundMessage.getMetadata(MessageMetadataType.SOURCE);
            if (reference instanceof OnlineMessageSource) {
            
                final OnlineMessageSource onlineMessageSource = (OnlineMessageSource) reference;
                final ContactOrBot target = onlineMessageSource.getTarget();
            
                if (target instanceof Bot) {
                    bot = (Bot) target;
                }
                if (target instanceof Contact) {
                    contact = (Contact) target;
                }
            }
        }
        if (Objects.nonNull(contact)) {
            return serializeToMessageSummary(Collections.singletonMap(Property.CONTACT, contact));
        }
        if (Objects.isNull(bot) && this instanceof BotObject) {
            final BotObject botObject = (BotObject) this;
            bot = botObject.getBot();
        }
    
        if (Objects.isNull(bot)) {
            return serializeToMessageSummary(Collections.emptyMap());
        } else {
            return serializeToMessageSummary(Collections.singletonMap(Property.BOT, bot));
        }
    }
    
    /**
     * 序列化为消息摘要
     *
     * @param properties 相关属性
     * @return 描述消息内容的字符串
     * @throws NullPointerException properties 为 null
     */
    String serializeToMessageSummary(Map<Property<?>, Object> properties);
}
