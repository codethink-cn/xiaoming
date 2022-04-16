package cn.codethink.xiaoming.resource;

import cn.codethink.common.util.Preconditions;
import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 表示文件资源
 *
 * @author Chuanwise
 */
@Data
public class FileResource
    implements Resource {
    
    protected File file;
    
    public FileResource(File file) {
        Preconditions.nonNull(file, "file");
        
        this.file = file;
    }
    
    @Override
    public InputStream open() throws FileNotFoundException {
        return new FileInputStream(file);
    }
}
