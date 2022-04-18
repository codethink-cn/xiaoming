package cn.codethink.xiaoming.resource;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.resource.Resource
 */
@Data
public class UrlResource
    implements Resource, AutoSerializable {
    
    private final URL url;
    
    public UrlResource(URL url) {
        Preconditions.objectNonNull(url, "url");
        
        this.url = url;
    }
    
    @Override
    public InputStream open() throws IOException {
        return url.openStream();
    }
}
