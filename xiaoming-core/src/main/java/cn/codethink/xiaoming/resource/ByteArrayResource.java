package cn.codethink.xiaoming.resource;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.resource.Resource
 */
@Data
public class ByteArrayResource
    implements Resource, AutoSerializable {
    
    private final byte[] bytes;
    
    public ByteArrayResource(byte[] bytes) {
        Preconditions.objectNonNull(bytes, "bytes");
        
        this.bytes = bytes;
    }
    
    @Override
    public InputStream open() throws IOException {
        return new ByteArrayInputStream(bytes);
    }
}
