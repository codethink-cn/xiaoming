package cn.codethink.xiaoming.message.basic;

import lombok.Data;

import java.util.Iterator;
import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Forward
 * @see ForwardBuilder
 */
@Data
public class ForwardImpl
    extends AbstractBasicMessage
    implements Forward {
    
    private final String title;
    
    private final String description;
    
    private final String source;
    
    private final String summary;
    
    private final List<String> preview;
    
    private final List<ForwardElement> elements;
    
    @Override
    public Iterator<ForwardElement> iterator() {
        return elements.iterator();
    }
}
