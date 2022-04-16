package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;

import java.io.InputStream;
import java.net.URL;

/**
 * 闪照消息
 *
 * @author Chuanwise
 */
public class FlashImage
    extends AbstractBasicMessage
    implements BasicMessage, Image {
    
    private final Image image;
    
    public FlashImage(Image image) {
        Preconditions.objectNonNull(image, "image");
    
        // guarantee the image is an actual image
        while (image instanceof FlashImage) {
            image = ((FlashImage) image).image;
        }
        this.image = image;
    }
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("flash")
            .argument(image.serializeToMessageCode())
            .build();
    }
    
    @Override
    public String getUrlString() {
        return image.getUrlString();
    }
    
    @Override
    public URL getUrl() {
        return image.getUrl();
    }
    
    @Override
    public InputStream open() throws Exception {
        return image.open();
    }
    
    @Override
    public String serializeToSummary() {
        return "[闪照]";
    }
}
