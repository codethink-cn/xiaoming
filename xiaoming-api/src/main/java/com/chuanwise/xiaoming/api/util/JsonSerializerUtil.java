package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Chuanwise
 */
public class JsonSerializerUtil {
    @Getter
    static final JsonSerializerUtil INSTANCE = new JsonSerializerUtil();

    ObjectMapper objectMapper = new ObjectMapper();

    private JsonSerializerUtil() {
        // 只使用公开的 setter
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        // 不使用 getter
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        // 直接填充 field
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 序列化不明确的类时，写上类名
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance , ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS, JsonTypeInfo.As.PROPERTY);
    }

    public <T> T readValue(final InputStream inputStream,
                           final Class<T> clazz)
            throws IOException {
        return objectMapper.readValue(inputStream, clazz);
    }

    public void writeValue(final OutputStream outputStream,
                           final Object object)
            throws IOException {
        objectMapper.writeValue(outputStream, object);
    }

    public <T> T convert(final Object o, Class<T> clazz) {
        return objectMapper.convertValue(o, clazz);
    }

    public String toJsonString(final Object o) {
        String string = null;
        try {
            string = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return string;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
