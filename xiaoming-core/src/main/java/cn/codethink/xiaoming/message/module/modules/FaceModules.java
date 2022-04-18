package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.message.basic.CustomFace;
import cn.codethink.xiaoming.message.basic.Image;
import cn.codethink.xiaoming.message.basic.PrimitiveFace;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.SerializeContext;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.util.MessageCodeTexts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Face
 */
public class FaceModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // primitive face
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(PrimitiveFace.class)
    List<String> serializePrimitiveFace(PrimitiveFace face) {
        return Collections.asUnmodifiableList(
            "face",
            "primitive",
            Integer.toString(face.getCode())
        );
    }
    
    @Deserializer("face:primitive:?")
    PrimitiveFace deserializePrimitiveFace(@DeserializerValue String code) {
        return PrimitiveFace.of(Integer.parseInt(code));
    }
    
    @Summarizer(PrimitiveFace.class)
    String summaryPrimitiveFace(PrimitiveFace face) {
        return "[" + face.getName() + "]";
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // custom face
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(CustomFace.class)
    List<String> serializeCustomFace(CustomFace face, SerializeContext context) {
        final List<String> list = Collections.asList("face", "custom");
        final List<String> imageList = MessageModule.serialize(face.getImage(), context.getProperties());
        list.addAll(imageList.subList(1, imageList.size()));
        return list;
    }
    
    @Deserializer("face:custom:??")
    CustomFace deserializeCustomFace(@DeserializerValue String code,
                                     DeserializeContext context) {
        
        // image code
        final List<String> arguments = new ArrayList<>();
        arguments.add("image");
        arguments.addAll(MessageCodeTexts.parseArguments(code));
    
        final Image image = (Image) MessageModule.deserialize(arguments, context.getProperties());
        return CustomFace.of(image);
    }
    
    @Summarizer(CustomFace.class)
    String summaryCustomFace() {
        return "[动画表情]";
    }
}
