package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.basic.Text;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.util.MessageCodeTexts;

import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Text
 */
public class TextModules {
    
    @Serializer(Text.class)
    List<String> serializeText(Text text) {
        return Collections.asUnmodifiableList(
            "text",
            MessageCodeTexts.toTextMessageCode(text.getText())
        );
    }
    
    @Deserializer("text:??")
    Text deserializeText(@DeserializerValue String text) {
        return Text.of(text);
    }
    
    @Summarizer(Text.class)
    String summaryText(Text text) {
        return text.getText();
    }
}
