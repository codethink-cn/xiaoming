package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Collections;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.Summarizable;
import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.metadata.MessageMetadataType;
import cn.codethink.xiaoming.property.Property;

import java.util.Map;
import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.compound.CompoundMessage
 * @see cn.codethink.xiaoming.message.Message
 */
public abstract class AbstractCompoundMessage
    implements CompoundMessage {
    
    private String summary;
    
    private String messageCode;
    
    @Override
    @SuppressWarnings("all")
    public String serializeToMessageCode(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");
        
        if (Objects.isNull(messageCode)) {
            final Map<MessageMetadataType, MessageMetadata> metadata = (Map) getMetadata();
            
            final String metadataMessageCode = Collections.toString(metadata.values(), x -> x.serializeToMessageCode(properties), "");
            final String basicMessageCode = Collections.toString(this, x -> x.serializeToMessageCode(properties), "");
            
            messageCode = metadataMessageCode + basicMessageCode;
        }
        return messageCode;
    }
    
    @Override
    public String serializeToMessageSummary(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");

        if (Objects.isNull(summary)) {
            summary = Collections.toString(this, x -> x.serializeToMessageSummary(properties), "");
        }
        return summary;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompoundMessage)) {
            return false;
        }
        final CompoundMessage that = (CompoundMessage) o;
    
        final int size = size();
        if (size != that.size()) {
            return false;
        }
    
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(get(i), that.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(Collections.asList(this), getMetadata());
    }
    
    @Override
    public String toString() {
        return serializeToMessageCode();
    }
}
