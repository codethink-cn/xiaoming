package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;
import lombok.Data;

import java.util.*;

/**
 * @author Chuanwise
 *
 * @see ImageCodec
 */
@Data
public class ImageCodecImpl
    implements ImageCodec {
    
    private static final Set<ImageCodec> INSTANCES = new HashSet<>();
    
    private static final Map<String, ImageCodec> NAME_INSTANCES = new HashMap<>();
    
    private final String extension;
    
    public ImageCodecImpl(String extension) {
        Preconditions.objectArgumentNonEmpty(extension, "extension");
        Preconditions.argument(!NAME_INSTANCES.containsKey(extension), "image type with extension '" + extension + "' already exist!");
        
        this.extension = extension;
        NAME_INSTANCES.put(extension, this);
    }
    
    public static Set<ImageCodec> getInstances() {
        return Collections.unmodifiableSet(INSTANCES);
    }
    
    public static ImageCodec getImageType(String name) {
        Preconditions.objectArgumentNonEmpty(name, "name");
        return Maps.getOrFail(NAME_INSTANCES, name);
    }
}
