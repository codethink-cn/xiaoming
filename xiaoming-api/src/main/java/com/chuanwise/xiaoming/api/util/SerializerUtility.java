package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.util.StaticUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Chuanwise
 */
public class SerializerUtility extends StaticUtils {
    /**
     * 设置基础的属性，包括但不限于写类名等
     * @param objectMapper JSON 解析器
     * @return 修改后的 JSON 解析器
     */
    public static ObjectMapper initialized(ObjectMapper objectMapper) {
        // 只使用公开的 setter
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        // 不使用 getter
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        // 直接填充 field
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 序列化不明确的类时，写上类名
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }
}