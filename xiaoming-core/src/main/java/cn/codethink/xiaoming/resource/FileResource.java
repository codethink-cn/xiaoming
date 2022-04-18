package cn.codethink.xiaoming.resource;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Chuanwise
 *
 * @see Resource
 */
@Data
public class FileResource
    implements Resource, AutoSerializable {
    
    private final File file;
    
    public FileResource(File file) {
        Preconditions.objectNonNull(file, "file");
        
        this.file = file;
    }
    
    @Override
    public InputStream open() throws IOException {
        return new FileInputStream(file);
    }
}
