package cn.codethink.xiaoming.resource;

import cn.codethink.common.util.Preconditions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * URL 资源
 *
 * @author Chuanwise
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlResource
    implements Resource {
    
    protected URL url;
    
    public UrlResource(URL url) {
        Preconditions.nonNull(url, "url");
        
        this.url = url;
    }
    
    @Override
    public InputStream open() throws IOException {
        return url.openStream();
    }
}
