package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.exception.ResourceException;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import cn.codethink.xiaoming.resource.Resource;
import lombok.Data;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Image
 * @see cn.codethink.xiaoming.message.basic.ResourceImage
 */
@Data
public class ResourceImageImpl
    extends AbstractBasicMessage
    implements ResourceImage {
    
    private final Resource resource;
    
    private final int width, height;
    
    private final long size;
    
    private final ImageType imageType;
    
    public ResourceImageImpl(Resource resource) {
        Preconditions.objectNonNull(resource, "resource");
        
        this.resource = resource;
    
        int tempHeight = 0;
        int tempWeight = 0;
        int tempSize = 0;
        ImageType tempImageType = null;
        
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(resource.open())) {
            
            // find image reader
            final Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            if (!iterator.hasNext()) {
                return;
            }
            final ImageReader reader = iterator.next();
    
            // get image type
            final String formatName = reader.getFormatName();
            try {
                tempImageType = ImageType.of(formatName);
            } catch (NoSuchElementException e) {
                tempImageType = new ImageTypeImpl(formatName);
            }
    
            // get height, weight, size
            final BufferedImage bufferedImage = ImageIO.read(imageInputStream);
            tempHeight = bufferedImage.getHeight();
            tempWeight = bufferedImage.getWidth();
            tempSize = bufferedImage.getData().getDataBuffer().getSize();
            
        } catch (Exception ignored) {
        } finally {
            this.height = tempHeight;
            this.width = tempWeight;
            this.size = tempSize;
            this.imageType = tempImageType;
        }
    }
    
    public ResourceImageImpl(Resource resource, int width, int height, int size, ImageType imageType) {
        Preconditions.objectNonNull(resource, "resource");
        Preconditions.objectNonNull(imageType, "image type");
        
        this.resource = resource;
        this.width = width;
        this.height = height;
        this.size = size;
        this.imageType = imageType;
    }
}
