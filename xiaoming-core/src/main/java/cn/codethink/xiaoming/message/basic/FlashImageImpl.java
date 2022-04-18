package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.FlashImage
 */
@Data
public class FlashImageImpl
    extends AbstractBasicMessage
    implements FlashImage {
    
    private final Image image;
    
    public FlashImageImpl(Image image) {
        Preconditions.objectNonNull(image, "image");
        
        this.image = image;
    }
}
