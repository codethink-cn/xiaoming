package cn.codethink.xiaoming.message.module.modules;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.message.basic.FlashImage;
import cn.codethink.xiaoming.message.basic.ResourceImage;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.SerializeContext;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.resource.Resource;
import cn.codethink.xiaoming.util.MessageCodeTexts;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.FlashImage
 */
public class FlashImageModules {
    
    @Serializer(FlashImage.class)
    List<String> serializeFlashImage(FlashImage image, SerializeContext context) {
        final List<String> list = new ArrayList<>(MessageModule.serialize(image.getImage(), context.getProperties()));
        list.set(0, "flash");
        return list;
    }
    
    @Deserializer("flash:??")
    FlashImage deserializeResourceImage(@DeserializerValue String value,
                                        DeserializeContext context) {
        final List<String> arguments = MessageCodeTexts.parseArguments(value);
        
        final List<String> newArguments = new ArrayList<>(arguments.size() + 1);
        newArguments.add("image");
        newArguments.addAll(arguments);
        
        final Resource resource = (Resource) MessageModule.deserialize(newArguments, context.getProperties());
        return FlashImage.of(ResourceImage.of(resource));
    }
    
    @Summarizer(FlashImage.class)
    String summaryFlashImage() {
        return "[闪照]";
    }
}
