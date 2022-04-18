package cn.codethink.xiaoming.message.module.summary;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.property.PropertyHolderImpl;
import lombok.Data;

import java.util.Map;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.module.summary.SummaryContext
 */
@Data
public class SummaryContextImpl
    extends PropertyHolderImpl
    implements SummaryContext {
    
    private final AutoSummarizable source;
    
    public SummaryContextImpl(AutoSummarizable source, Map<Property<?>, Object> properties) {
        super(properties);
    
        Preconditions.objectNonNull(source, "source");
        
        this.source = source;
    }
    
    @Override
    public Contact getContact() {
        return getProperty(Property.CONTACT);
    }
}
