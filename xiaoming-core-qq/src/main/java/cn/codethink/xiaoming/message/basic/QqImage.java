package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.resource.QqImageResource;
import cn.codethink.xiaoming.resource.Resource;
import cn.codethink.xiaoming.util.Qqs;
import lombok.Getter;

import java.util.Objects;

/**
 * qq 图片
 *
 * @author Chuanwise
 */
public class QqImage
    extends AbstractBasicMessage
    implements ResourceImage, AutoSerializable, AutoSummarizable, CacheImage {
    
    @Getter
    private final net.mamoe.mirai.message.data.Image image;
    
    private boolean imageTypeSet;
    
    private ImageCodec imageCodec;
    
    private Resource resource;
    
    public QqImage(net.mamoe.mirai.message.data.Image image) {
        Preconditions.objectNonNull(image, "image");
        
        this.image = image;
    }
    
    public ImageCodec getImageCodec() {
        if (!imageTypeSet) {
            imageCodec = Qqs.toXiaoMing(image.getImageType());
            imageTypeSet = true;
        }
        return imageCodec;
    }
    
    @Override
    public int getWidth() {
        return image.getWidth();
    }
    
    @Override
    public int getHeight() {
        return image.getHeight();
    }
    
    @Override
    public long getSize() {
        return image.getSize();
    }
    
    @Override
    public Resource getResource() {
        if (Objects.isNull(resource)) {
            resource = new QqImageResource(image);
        }
        return resource;
    }
}
