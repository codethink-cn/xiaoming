package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.basic.Forward;
import cn.codethink.xiaoming.message.basic.ForwardElement;
import cn.codethink.xiaoming.message.basic.ForwardElementImpl;
import cn.codethink.xiaoming.message.basic.ForwardImpl;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.util.MessageCode;
import cn.codethink.xiaoming.util.MessageCodeTexts;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Forward
 */
public class ForwardModules {
    
    @Serializer(Forward.class)
    List<String> serializeForward(Forward forward) {
        final List<String> list = Collections.asList(
            "forward",
            forward.getDescription(),
            forward.getSource(),
            forward.getSummary(),
            Collections.toString(forward.getPreview(), MessageCodeTexts::serializeComma, ",")
        );
    
        for (ForwardElement element : forward.getElements()) {
            list.add(
                MessageCodeTexts.serializeComma(
                    element.getSenderName() + "," +
                        element.getSenderCode() + "," +
                        element.getTimestamp() + "," +
                        element.getMessage().asCompoundMessage().serializeToMessageCode()
                )
            );
        }
        
        return list;
    }
    
    @Deserializer("forward:?:?:?:?:?:??")
    Forward deserializeForward(@DeserializerValue String title,
                               @DeserializerValue String description,
                               @DeserializerValue String source,
                               @DeserializerValue String summary,
                               @DeserializerValue String preview,
                               @DeserializerValue String elements,
                               DeserializeContext context) {
    
        final List<String> finalPreview = MessageCodeTexts.deserializeComma(preview);
        final List<ForwardElement> finalElements = MessageCodeTexts.parseArguments(elements)
            .stream()
            .map(text -> {
                final List<String> strings = MessageCodeTexts.deserializeComma(text);
                Preconditions.argument(strings.size() == 4, "illegal forward element");
            
                return new ForwardElementImpl(
                    Code.parseCode(strings.get(0)),
                    strings.get(1),
                    Long.parseLong(strings.get(2)),
                    MessageCode.deserializeMessageCode(strings.get(3), context.getProperties())
                );
            })
            .collect(Collectors.toList());
        
        return new ForwardImpl(
            title,
            description,
            source,
            summary,
            finalPreview,
            finalElements
        );
    }
    
    @Summarizer(Forward.class)
    String summaryForward(Forward forward) {
        return forward.getSummary();
    }
}
