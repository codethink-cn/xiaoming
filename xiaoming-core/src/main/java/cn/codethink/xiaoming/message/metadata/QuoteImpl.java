package cn.codethink.xiaoming.message.metadata;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.property.Property;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.metadata.Quote
 */
@Data
public class QuoteImpl
    implements Quote {
    
    private final MessageSource messageSource;
    
    public QuoteImpl(MessageSource messageSource) {
        Preconditions.objectNonNull(messageSource, "message messageSource");
        
        this.messageSource = messageSource;
    }
    
    @Override
    public Property<?> getMetadataType() {
        return MessageMetadataType.SOURCE;
    }
}
