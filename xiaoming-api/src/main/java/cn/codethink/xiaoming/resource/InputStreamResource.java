package cn.codethink.xiaoming.resource;

import cn.codethink.common.util.Preconditions;
import lombok.Data;

import java.io.InputStream;

/**
 * 输入流资源
 *
 * @author Chuanwise
 */
@Data
public class InputStreamResource
    implements Resource {
    
    private final InputStream inputStream;
    
    public InputStreamResource(InputStream inputStream) {
        Preconditions.nonNull(inputStream, "input stream");
        
        this.inputStream = inputStream;
    }
    
    @Override
    public InputStream open() throws Exception {
        return inputStream;
    }
}
