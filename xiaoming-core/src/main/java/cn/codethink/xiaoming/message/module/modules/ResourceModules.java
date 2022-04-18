package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.resource.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.resource.Resource
 */
public class ResourceModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // url
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(UrlResource.class)
    List<String> serializeUrlResource(UrlResource resource) {
        return Collections.asUnmodifiableList(
            "resource",
            "url",
            resource.getUrl().toExternalForm()
        );
    }
    
    @Deserializer("resource:url:??")
    UrlResource deserializeUrlResource(@DeserializerValue String url) throws MalformedURLException {
        return (UrlResource) Resource.of(new URL(url));
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // byte array
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(ByteArrayResource.class)
    List<String> serializeByteArrayResource(ByteArrayResource resource) {
        return Collections.asUnmodifiableList(
            "resource",
            "base64",
            Base64.getEncoder().encodeToString(resource.getBytes())
        );
    }
    
    @Deserializer("resource:base64:?")
    ByteArrayResource deserializeByteArrayResource(@DeserializerValue String base64) {
        return (ByteArrayResource) Resource.of(Base64.getDecoder().decode(base64));
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // class path
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(ClassPathResource.class)
    List<String> serializeClassPathResource(ClassPathResource resource) {
        return Collections.asUnmodifiableList(
            "resource",
            "class",
            resource.getClazz().getName(),
            resource.getPath()
        );
    }
    
    @Deserializer("resource:class:?:?")
    ClassPathResource deserializeClassPathResource(@DeserializerValue String className,
                                                   @DeserializerValue String path) throws ClassNotFoundException {
    
        final Class<?> clazz = Class.forName(className);
        return (ClassPathResource) Resource.of(clazz, path);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // file
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(FileResource.class)
    List<String> serializeFileResource(FileResource resource) {
        return Collections.asUnmodifiableList(
            "resource",
            "file",
            resource.getFile().getPath()
        );
    }
    
    @Deserializer("resource:file:?")
    FileResource deserializeFileResource(@DeserializerValue String path) {
        return (FileResource) Resource.of(new File(path));
    }
}
