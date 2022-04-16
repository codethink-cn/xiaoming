package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.Collections;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.util.Texts;

import java.util.*;

/**
 * 消息码构建器
 *
 * @author Chuanwise
 */
public class MessageCodeBuilder {
    
    /**
     * 消息类型
     */
    private final String type;
    
    /**
     * 参数列表
     */
    private final List<Object> arguments = new ArrayList<>();
    
    protected MessageCodeBuilder(String type) {
        Preconditions.objectArgumentNonEmpty(type, "type");
        
        this.type = type;
    }
    
    public MessageCodeBuilder argument(Object argument) {
        
        arguments.add(argument);
        
        return this;
    }
    
    public MessageCodeBuilder arguments(Object... arguments) {
        Preconditions.objectNonNull(arguments, "arguments");

        this.arguments.addAll(Arrays.asList(arguments));
        
        return this;
    }
    
    public MessageCodeBuilder arguments(Iterable<? extends Object> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        for (Object object : iterable) {
            arguments.add(object);
        }
        
        return this;
    }
    
    /**
     * 构建消息码。格式是 [type] 或 [type:arg1,arg2,arg3]
     *
     * @return 消息码
     */
    public String build() {
        if (arguments.isEmpty()) {
            return "[" + Texts.serializeText(type) + "]";
        } else {
            return "[" + Texts.serializeText(type) + ":" +
                Collections.toString(arguments, x -> Texts.serializeText(Objects.toString(x)), ",") + "]";
        }
    }
    
    @Override
    public String toString() {
        return build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageCodeBuilder that = (MessageCodeBuilder) o;
        return Objects.equals(type, that.type) && Objects.equals(arguments, that.arguments);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, arguments);
    }
}
