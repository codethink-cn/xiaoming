package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.Strings;
import cn.codethink.xiaoming.Bot;
import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.message.basic.BasicMessage;
import cn.codethink.xiaoming.message.basic.Text;
import cn.codethink.xiaoming.message.parser.MessageParsers;
import cn.codethink.xiaoming.message.parser.DefaultBasicMessageParser;

import java.util.*;

/**
 * 消息码相关的工具
 *
 * @author Chuanwise
 */
public class MessageCode
    extends StaticUtilities {
    
    /**
     * 转义字符
     */
    private static final char TRANSLATED_CHARACTER = '\\';
    
    public static MessageCodeBuilder builder(String messageType) {
        return new MessageCodeBuilder(messageType);
    }
    
    static {
        // register basic parsers
        MessageParsers.registerParsers(DefaultBasicMessageParser.getInstance());
    }
    
    /**
     * 反序列化状态机状态
     */
    private enum State {
    
        /**
         * 文本状态
         */
        TEXT,
    
        /**
         * 类型状态
         */
        TYPE,
    
        /**
         * 值状态
         */
        ARGUMENT,
    }
    
    /**
     * 反序列化消息码为消息体
     *
     * @param messageCode 消息码
     * @param bot 消息体关联的 Bot
     * @return 消息体
     */
    @SuppressWarnings("all")
    public static CompoundMessage deserializeToCompoundMessage(String messageCode, Bot bot) {
        Preconditions.objectArgumentNonEmpty(messageCode, "message code");
//        Preconditions.objectNonNull(bot, "bot");
        // TODO: 2022/4/16 null check
        
        final CompoundMessageBuilder compoundMessageBuilder = CompoundMessageBuilder.builder();
        
        final StringBuilder stringBuilder = new StringBuilder();
        final List<String> arguments = new ArrayList<>();
    
        State state = State.TEXT;
        final int length = messageCode.length();
        
        boolean escaped = false;
        
        // buffer
        String type = null;
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
                    case '\\':
                        stringBuilder.append("\\");
                        break;
                    default:
                        stringBuilder.append(ch);
                }
                escaped = false;
                continue;
            }
            if (ch == TRANSLATED_CHARACTER) {
                escaped = true;
                continue;
            }
    
            switch (state) {
                case TEXT: {
                    if (ch == '[') {
                        state = State.TYPE;
                        
                        // if text is not empty
                        if (stringBuilder.length() > 0) {
                            final String text = stringBuilder.toString();
                            stringBuilder.setLength(0);
                            
                            compoundMessageBuilder.plus(new Text(text));
                        }
                    } else {
                        stringBuilder.append(ch);
                    }
                    break;
                }
                case TYPE: {
                    if (ch == ':') {
                        state = State.ARGUMENT;
                        type = stringBuilder.toString();
                        stringBuilder.setLength(0);
    
                        // check length
                        check(!type.isEmpty(), messageCode, i, "basic message type is empty");
                    } else if (ch == ']') {
                        state = State.TEXT;
                        type = stringBuilder.toString();
                        stringBuilder.setLength(0);
    
                        // check length
                        check(!type.isEmpty(), messageCode, i, "basic message type is empty");
    
                        try {
                            compoundMessageBuilder.plus(parseMessage(type, Collections.emptyList(), bot));
                        } catch (Exception e) {
                            report(messageCode, i, e);
                            return null;
                        }
                    } else {
                        stringBuilder.append(ch);
                    }
                    break;
                }
                case ARGUMENT: {
                    if (ch == ',') {
                        final String argument = stringBuilder.toString();
                        stringBuilder.setLength(0);
                        
                        arguments.add(argument);
                    } else if (ch == ']') {
                        state = State.TEXT;
                        
                        final String argument = stringBuilder.toString();
                        stringBuilder.setLength(0);
                        arguments.add(argument);
    
                        final Message message;
                        try {
                            message = parseMessage(type, arguments, bot);
                        } catch (Exception e) {
                            report(messageCode, i, e);
                            return null;
                        }
                        if (message instanceof BasicMessage) {
                            final BasicMessage basicMessage = (BasicMessage) message;
                            compoundMessageBuilder.plus(basicMessage);
                        }
                        
                        arguments.clear();
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
                    compoundMessageBuilder.plus(new Text(stringBuilder.toString()));
                }
                break;
            }
            case TYPE:
            case ARGUMENT:
                report(messageCode, messageCode.length(), "endchar ']' required");
                return null;
            default:
                throw new IllegalStateException();
        }
        
        return compoundMessageBuilder.build();
    }
    
    private static Message parseMessage(String type, List<String> arguments, Bot bot) throws Exception {
        final Message basicMessage = MessageParsers.parseMessage(type, arguments, bot);
        cn.chuanwise.common.util.Preconditions.nonNull(basicMessage, "parse failed");
        return basicMessage;
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
}