package cn.codethink.xiaoming.resource;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Chuanwise
 *
 * @see Resource
 */
@Data
public class ClassPathResource
    implements Resource, AutoSerializable {
    
    private final Class<?> clazz;
    
    private final String path;
    
    public ClassPathResource(Class<?> clazz, String path) {
        Preconditions.objectNonNull(clazz, "class");
        Preconditions.objectArgumentNonEmpty(path, "path");
        
        this.clazz = clazz;
        this.path = path;
    }
    
    @Override
    public InputStream open() throws IOException {
        return clazz.getResourceAsStream(path);
    }
}
