package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.resource.Resource;
import lombok.Data;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
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
    
    private final ImageCodec imageCodec;
    
    public ResourceImageImpl(Resource resource) {
        Preconditions.objectNonNull(resource, "resource");
        
        this.resource = resource;
    
        int tempHeight = 0;
        int tempWeight = 0;
        int tempSize = 0;
        ImageCodec tempImageCodec = null;
        
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
                tempImageCodec = ImageCodec.of(formatName);
            } catch (NoSuchElementException e) {
                tempImageCodec = new ImageCodecImpl(formatName);
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
            this.imageCodec = tempImageCodec;
        }
    }
    
    public ResourceImageImpl(Resource resource, int width, int height, int size, ImageCodec imageCodec) {
        Preconditions.objectNonNull(resource, "resource");
        Preconditions.objectNonNull(imageCodec, "image type");
        
        this.resource = resource;
        this.width = width;
        this.height = height;
        this.size = size;
        this.imageCodec = imageCodec;
    }
}
