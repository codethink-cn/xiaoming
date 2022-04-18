package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.ByteStreams;
import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.exception.ResourceException;
import cn.codethink.xiaoming.message.basic.Image;
import cn.codethink.xiaoming.message.basic.ResourceImage;
import cn.codethink.xiaoming.message.basic.ResourceImageImpl;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.SerializeContext;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.resource.Resource;
import cn.codethink.xiaoming.util.MessageCodeTexts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Image
 */
public class ImageModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // simple image
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(Image.class)
    List<String> serializeImage(ResourceImage image, SerializeContext context) throws ResourceException {
        final List<String> list = Collections.asList("image");
        final Resource resource = image.getResource();
    
        // try to serialize resource
        try {
            list.addAll(MessageModule.serialize(resource, context.getProperties()));
            return list;
        } catch (Exception ignored) {
        }
        
        // try to read resource
        try (InputStream inputStream = resource.open()) {
            final byte[] bytes = ByteStreams.read(inputStream);
            final String base64 = Base64.getEncoder().encodeToString(bytes);
            
            list.add("base64");
            list.add(base64);
            return list;
        } catch (IOException exception) {
            throw new ResourceException("read resource failed for " + resource, exception);
        }
    }
    
    @Summarizer(Image.class)
    String summaryImage(Image image) {
        return "[图片]";
    }
    
    @Deserializer("image:resource:??")
    ResourceImage deserializeResourceImage(@DeserializerValue String value,
                                           DeserializeContext context) {
        final List<String> arguments = MessageCodeTexts.parseArguments(value);
        
        final List<String> newArguments = new ArrayList<>(arguments.size() + 1);
        newArguments.add("resource");
        newArguments.addAll(arguments);
    
        final Resource resource = (Resource) MessageModule.deserialize(newArguments, context.getProperties());
        return ResourceImage.of(resource);
    }
    
    @Deserializer("image:base64:??")
    ResourceImage deserializeBase64Image(@DeserializerValue String base64) {
        final Resource resource = Resource.of(Base64.getDecoder().decode(base64));
        return ResourceImage.of(resource);
    }
}
