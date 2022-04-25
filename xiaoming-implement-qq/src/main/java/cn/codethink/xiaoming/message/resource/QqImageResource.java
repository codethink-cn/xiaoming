package cn.codethink.xiaoming.message.resource;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.basic.QqImage;
import cn.codethink.xiaoming.resource.Resource;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.resource.Resource
 * @see QqImage
 */
@Data
public class QqImageResource
    implements Resource, AutoSerializable {
    
    private final Image image;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private URL url;
    
    public QqImageResource(Image image) {
        Preconditions.objectNonNull(image, "image");
        
        this.image = image;
    }
    
    @Override
    public InputStream open() throws IOException {
        if (Objects.isNull(url)) {
            url = new URL(Image.queryUrl(image));
        }
        return url.openStream();
    }
}
