package cn.codethink.xiaoming.message.element;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.MessageCode;
import lombok.Data;

import java.io.InputStream;
import java.net.URL;

/**
 * 通过 URL 定位的图片。将会被序列化为 {@code [image:url:$url]}
 *
 * @author Chuanwise
 */
@Data
public class UrlImage
    extends AbstractBasicMessage
    implements Image {
    
    private final URL url;
    
    public UrlImage(URL url) {
        Preconditions.objectNonNull(url, "url");
        
        this.url = url;
    }
    
    @Override
    public String serializeToMessageCode() {
        return MessageCode.builder("image")
            .argument("url")
            .argument(getUrlString())
            .build();
    }
    
    @Override
    public String getUrlString() {
        return url.toExternalForm();
    }
    
    @Override
    public InputStream open() throws Exception {
        return url.openStream();
    }
}
