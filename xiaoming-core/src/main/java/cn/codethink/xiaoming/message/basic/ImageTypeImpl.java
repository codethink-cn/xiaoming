package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;
import lombok.Data;

import java.util.*;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.ImageType
 */
@Data
public class ImageTypeImpl
    implements ImageType {
    
    private static final Set<ImageType> INSTANCES = new HashSet<>();
    
    private static final Map<String, ImageType> NAME_INSTANCES = new HashMap<>();
    
    private final String extension;
    
    public ImageTypeImpl(String extension) {
        Preconditions.objectArgumentNonEmpty(extension, "extension");
        Preconditions.argument(!NAME_INSTANCES.containsKey(extension), "image type with extension '" + extension + "' already exist!");
        
        this.extension = extension;
        NAME_INSTANCES.put(extension, this);
    }
    
    public static Set<ImageType> getInstances() {
        return Collections.unmodifiableSet(INSTANCES);
    }
    
    public static ImageType getImageType(String name) {
        Preconditions.objectArgumentNonEmpty(name, "name");
        return Maps.getOrFail(NAME_INSTANCES, name);
    }
}
