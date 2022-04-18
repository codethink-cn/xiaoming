package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.resource.MiraiImageResource;
import cn.codethink.xiaoming.resource.Resource;
import cn.codethink.xiaoming.util.Mirais;
import lombok.Getter;

import java.util.Objects;

/**
 * mirai 图片
 *
 * @author Chuanwise
 */
public class MiraiImage
    extends AbstractBasicMessage
    implements ResourceImage, AutoSerializable, AutoSummarizable, CacheImage {
    
    @Getter
    private final net.mamoe.mirai.message.data.Image image;
    
    private boolean imageTypeSet;
    
    private ImageType imageType;
    
    private Resource resource;
    
    public MiraiImage(net.mamoe.mirai.message.data.Image image) {
        Preconditions.objectNonNull(image, "image");
        
        this.image = image;
    }
    
    @Override
    public ImageType getImageType() {
        if (!imageTypeSet) {
            imageType = Mirais.toXiaoMing(image.getImageType());
            imageTypeSet = true;
        }
        return imageType;
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
            resource = new MiraiImageResource(image);
        }
        return resource;
    }
}
