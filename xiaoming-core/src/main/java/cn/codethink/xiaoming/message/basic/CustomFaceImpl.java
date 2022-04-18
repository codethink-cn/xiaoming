package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Face
 * @see cn.codethink.xiaoming.message.basic.CustomFace
 */
@Data
public class CustomFaceImpl
    extends AbstractBasicMessage
    implements CustomFace {
    
    private final Image image;
    
    public CustomFaceImpl(Image image) {
        Preconditions.objectNonNull(image, "image");
        
        this.image = image;
    }
    
    @Override
    public String getName() {
        return "动画表情";
    }
}
