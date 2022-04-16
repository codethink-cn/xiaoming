package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
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
        return new MessageCodeBuilder("image")
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
