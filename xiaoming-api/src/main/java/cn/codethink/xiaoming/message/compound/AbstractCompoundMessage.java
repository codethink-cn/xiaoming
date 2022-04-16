package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.Summarizable;

import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.compound.CompoundMessage
 * @see cn.codethink.xiaoming.message.Message
 */
public abstract class AbstractCompoundMessage
    implements CompoundMessage {
    
    private String messageCode;
    
    private String summary;
    
    @Override
    public String serializeToMessageCode() {
        if (Objects.isNull(messageCode)) {
            messageCode = Collections.toString(getMetadata().values(), Serializable::serializeToMessageCode, "") +
                Collections.toString(this, Serializable::serializeToMessageCode, "");
        }
        return messageCode;
    }
    
    @Override
    public String serializeToSummary() {
        if (Objects.isNull(summary)) {
            summary = Collections.toString(this, Summarizable::serializeToSummary, "");
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
