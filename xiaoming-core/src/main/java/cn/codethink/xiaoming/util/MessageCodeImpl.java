package cn.codethink.xiaoming.util;

import cn.chuanwise.common.util.Strings;
import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import cn.codethink.xiaoming.message.basic.BasicMessage;
import cn.codethink.xiaoming.message.basic.Text;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 消息码相关的工具
 *
 * @author Chuanwise
 */
public class MessageCodeImpl
    extends StaticUtilities
    implements MessageCode {
    
    /**
     * 转义字符
     */
    private static final char TRANSLATED_CHARACTER = '\\';
    
    /**
     * 反序列化状态机状态
     */
    private enum State {
    
        /**
         * 文本状态
         */
        TEXT,
    
        /**
         * 值状态
         */
        ARGUMENT,
    }
    
    private static void report(String messageCode, int index, String message) {
        throw new IllegalArgumentException("deserialize error: " + message + "\n" +
            "message code: " + messageCode + " (position: " + index + ")\n" +
            Strings.repeat(' ', 13 + index) + "~~~");
    }
    
    private static void report(String messageCode, int index, Throwable cause) {
        throw new IllegalArgumentException("deserialize error: " + cause.getMessage() + "\n" +
            "message code: " + messageCode + " (position: " + index + ")\n" +
            Strings.repeat(' ', 13 + index) + "~~~", cause);
    }
    
    private static void check(boolean legal, String messageCode, int index, String message) {
        if (!legal) {
            report(messageCode, index, message);
        }
    }
    
    /**
     * 反序列化消息码为消息体
     *
     * @param messageCode 消息码
     * @param bot         消息体关联的 Bot
     * @return 消息体
     */
    @SuppressWarnings("all")
    public static CompoundMessage deserializeMessageCode(String messageCode, Map<Property<?>, Object> properties) {
        Preconditions.objectArgumentNonEmpty(messageCode, "message code");
        Preconditions.objectNonNull(properties, "properties");
        
        final CompoundMessageBuilder compoundMessageBuilder = CompoundMessageBuilder.newInstance();
        
        final StringBuilder stringBuilder = new StringBuilder();
        final List<String> arguments = new ArrayList<>();
        
        MessageCodeImpl.State state = MessageCodeImpl.State.TEXT;
        final int length = messageCode.length();
        
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            final char ch = messageCode.charAt(i);
            
            // escape
            if (escaped) {
                switch (ch) {
                    case 'b':
                        stringBuilder.append("\b");
                        break;
                    case 'f':
                        stringBuilder.append("\f");
                        break;
                    case 'n':
                        stringBuilder.append("\n");
                        break;
                    case 'r':
                        stringBuilder.append("\r");
                        break;
                    case 't':
                        stringBuilder.append("\t");
                        break;
                    default:
                        stringBuilder.append(ch);
                }
                escaped = false;
                continue;
            }
            if (ch == MessageCodeImpl.TRANSLATED_CHARACTER) {
                escaped = true;
                continue;
            }
            
            switch (state) {
                case TEXT: {
                    if (ch == '[') {
                        state = MessageCodeImpl.State.ARGUMENT;
                        
                        // if text is not empty
                        if (stringBuilder.length() > 0) {
                            final String text = stringBuilder.toString();
                            stringBuilder.setLength(0);
                            
                            compoundMessageBuilder.plus(Text.of(text));
                        }
                    } else {
                        stringBuilder.append(ch);
                    }
                    break;
                }
                case ARGUMENT: {
                    if (ch == ':') {
                        final String argument = stringBuilder.toString();
                        stringBuilder.setLength(0);
                        
                        arguments.add(argument);
                    } else if (ch == ']') {
                        state = MessageCodeImpl.State.TEXT;
                        
                        final String argument = stringBuilder.toString();
                        stringBuilder.setLength(0);
                        arguments.add(argument);
                        
                        final Object message;
                        try {
                            message = MessageModule.deserialize(arguments, properties);
                        } catch (Exception e) {
                            MessageCodeImpl.report(messageCode, i, e);
                            return null;
                        }
                        
                        arguments.clear();
                        
                        if (message instanceof BasicMessage) {
                            final BasicMessage basicMessage = (BasicMessage) message;
                            compoundMessageBuilder.plus(basicMessage);
                            break;
                        }
                        if (message instanceof MessageMetadata) {
                            final MessageMetadata messageMetadata = (MessageMetadata) message;
                            compoundMessageBuilder.plus(messageMetadata);
                            break;
                        }
                        
                        MessageCodeImpl.report(messageCode, i, "deserialized object is not a BasicMessage or MessageMetadata: " + message);
                        
                    } else {
                        stringBuilder.append(ch);
                    }
                    break;
                }
                default:
                    throw new IllegalStateException();
            }
        }
        
        switch (state) {
            case TEXT: {
                if (stringBuilder.length() > 0) {
                    compoundMessageBuilder.plus(Text.of(stringBuilder.toString()));
                }
                break;
            }
            case ARGUMENT:
                MessageCodeImpl.report(messageCode, messageCode.length(), "endchar ']' required");
                return null;
            default:
                throw new IllegalStateException();
        }
        
        return compoundMessageBuilder.build();
    }
}