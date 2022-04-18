package cn.codethink.xiaoming.message.module.deserialize;

import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see MethodDeserializerHandler
 */
@SuppressWarnings("all")
public interface DeserializerFormatElement {
    
    enum Parameter
        implements DeserializerFormatElement {
        
        INSTANCE
    }
    
    @Data
    class PlainText
        implements DeserializerFormatElement {
        
        private final String text;
    }
    
    enum RemainParameter
        implements DeserializerFormatElement {
        
        INSTANCE
    }
}
