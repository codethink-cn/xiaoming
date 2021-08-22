package cn.chuanwise.xiaoming.utility;

import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.toolkit.serialize.serializer.json.JackJsonSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

public class SerializerUtility {
    public static Serializer initializedSerializer() {
        final ObjectMapper objectMapper = new ObjectMapper();

        // 只使用公开的 setter
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        // 不使用 getter
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        // 直接填充 field
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 序列化不明确的类时，写上类名
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS, JsonTypeInfo.As.PROPERTY);

        return new JackJsonSerializer(objectMapper);
    }
}
