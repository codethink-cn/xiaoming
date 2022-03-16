package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.content.MessageContent;
import cn.codethink.xiaoming.message.content.SimpleMessageContent;
import cn.codethink.xiaoming.message.element.AccountAt;
import cn.codethink.xiaoming.message.element.AtAll;
import cn.codethink.xiaoming.message.element.MessageElement;
import cn.codethink.xiaoming.message.element.Text;
import cn.codethink.util.Preconditions;
import cn.codethink.util.StaticUtilities;
import cn.codethink.util.Strings;

import java.util.*;

public class MessageCode
    extends StaticUtilities {
    
    @SuppressWarnings("all")
    public static MessageContent deserialize(String message) {
        Preconditions.namedArgumentNonEmpty(message, "message");
        
        // 缓冲区
        final StringBuilder textBuffer = new StringBuilder();
        final StringBuilder typeBuffer = new StringBuilder();
        final StringBuilder propertyNameBuffer = new StringBuilder();
        final StringBuilder propertyValueBuffer = new StringBuilder();
        final Map<String, String> propertiesBuffer = new HashMap<>();
        
        final List<MessageElement> messageElements = new ArrayList<>();
        final int length = message.length();
        
        // 是否是转义字符
        boolean escaped = false;
        
        // 状态量
        int state = 0;
        final int textState = 0;
        final int typeState = 1;
        final int propertyNameState = 2;
        final int propertyValueState = 3;
        
        for (int i = 0; i < length; i++) {
            final char ch = message.charAt(i);
            
            if (escaped) {
                textBuffer.append(ch);
                escaped = false;
                continue;
            }
    
            switch (state) {
                case textState: {
                    switch (ch) {
                        case '\\':
                            escaped = true;
                            continue;
                        case '[':
                            state = typeState;
    
                            final String string = textBuffer.toString();
                            if (Strings.nonEmpty(string)) {
                                textBuffer.setLength(0);
                                messageElements.add(new Text(string));
                            }
    
                            continue;
                        default:
                            textBuffer.append(ch);
                    }
                }
                    break;
                case typeState: {
                    switch (ch) {
                        case ']': {
                            state = textState;
    
                            // 目前的消息是 [type] 类型的
                            // 故获取 type 并讨论
                            final String type = typeBuffer.toString();
                            typeBuffer.setLength(0);
    
                            switch (type) {
                                case "atall":
                                    messageElements.add(AtAll.getInstance());
                                    break;
                                default:
                                    report(message, i, "消息码无内容");
                            }
                        }
                            break;
                        case ':': {
                            state = propertyNameState;
                            
                            check(typeBuffer.length() > 0, message, i, "消息码无类型");
                        }
                            break;
                        default:
                            typeBuffer.append(ch);
                    }
                }
                    break;
                case propertyNameState: {
                    switch (ch) {
                        case '=': {
                            state = propertyValueState;
                            
                            check(propertyNameBuffer.length() > 0, message, i, "消息码属性名为空");
                        }
                            break;
                        case ',': {
                            state = propertyNameState;
                            
                            check(propertyNameBuffer.length() > 0, message, i, "消息码属性名为空");
                            final String propertyName = propertyNameBuffer.toString();
                            propertyNameBuffer.setLength(0);
                            
                            // 检查是否已经有该属性
                            check(!propertiesBuffer.containsKey(propertyName), message, i, "属性 \"" + propertyName + "\" 重复出现");
                            
                            // 设置为 bool 的 true
                            propertiesBuffer.put(propertyName, "true");
                        }
                            break;
                        case ']': {
                            state = textState;
                            
                            // 设置为属性值
                            check(propertyNameBuffer.length() > 0, message, i, "消息码属性名为空");
                            final String propertyName = propertyNameBuffer.toString();
                            propertyNameBuffer.setLength(0);
    
                            propertiesBuffer.put(propertyName, "true");
    
                            // 解析元素
                            final String type = typeBuffer.toString();
                            final MessageElement messageElement = parseMessageElement(type, propertiesBuffer);
                            if (Objects.nonNull(messageElement)) {
                                messageElements.add(messageElement);
                                propertiesBuffer.clear();
                                break;
                            }
    
                            // 再试试是不是元数据
                            final MessageContent messageContent = parseMessageContent(type, propertiesBuffer);
                            if (Objects.nonNull(messageContent)) {
                                propertiesBuffer.clear();
                                return messageContent;
                            }
    
                            // 错误
                            report(message, i, "消息码无法识别：" + type);
                        }
                            break;
                        default:
                            propertyNameBuffer.append(ch);
                    }
                }
                    break;
                case propertyValueState: {
                    switch (ch) {
                        case ',': {
                            state = propertyNameState;
    
                            // 属性名不需要检查，因为已经检查过了
                            final String propertyName = propertyNameBuffer.toString();
                            propertyNameBuffer.setLength(0);
    
                            // 检查是否已经有该属性
                            check(!propertiesBuffer.containsKey(propertyName), message, i, "属性 \"" + propertyName + "\" 重复出现");
    
                            // 属性值需要检查一下
                            check(propertyValueBuffer.length() > 0, message, i, "属性值 \"" + propertyName + "\" 为空");
                            final String propertyValue = propertyValueBuffer.toString();
                            propertyValueBuffer.setLength(0);
    
                            // 设置为属性值
                            propertiesBuffer.put(propertyName, propertyValue);
                        }
                            break;
                        case ']': {
                            state = textState;
    
                            // 属性名不需要检查，因为已经检查过了
                            final String propertyName = propertyNameBuffer.toString();
                            propertyNameBuffer.setLength(0);
    
                            // 检查是否已经有该属性
                            check(!propertiesBuffer.containsKey(propertyName), message, i, "属性 \"" + propertyName + "\" 重复出现");
    
                            // 属性值需要检查一下
                            check(propertyValueBuffer.length() > 0, message, i, "属性值 \"" + propertyName + "\" 为空");
                            final String propertyValue = propertyValueBuffer.toString();
                            propertyValueBuffer.setLength(0);
    
                            // 设置为属性值
                            propertiesBuffer.put(propertyName, propertyValue);
                            
                            // 解析元素
                            final String type = typeBuffer.toString();
                            final MessageElement messageElement = parseMessageElement(type, propertiesBuffer);
                            if (Objects.nonNull(messageElement)) {
                                propertiesBuffer.clear();
                                messageElements.add(messageElement);
                                break;
                            }
                            
                            // 再试试是不是元数据
                            final MessageContent messageContent = parseMessageContent(type, propertiesBuffer);
                            if (Objects.nonNull(messageContent)) {
                                propertiesBuffer.clear();
                                return messageContent;
                            }
                            
                            // 错误
                            report(message, i, "消息码无法识别：" + type);
                        }
                            break;
                        default:
                            propertyValueBuffer.append(ch);
                    }
                }
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        
        check(!escaped, message, message.length(), "转义字符不完整");
        check(state == textState, message, message.length(), "消息不完整");
    
        if (textBuffer.length() > 0) {
            messageElements.add(new Text(textBuffer.toString()));
        }
        
        return SimpleMessageContent.builder()
            .plusAll(messageElements)
            .build();
    }
    
    public static String serialize(MessageContent messageContent) {
        Preconditions.namedArgumentNonNull(messageContent, "message content");
    
        return messageContent.toMessageCode();
    }
    
    private static MessageElement parseMessageElement(String type, Map<String, String> properties) {
        switch (type) {
            case "at": {
                final String account = properties.get("account");
                if (Objects.nonNull(account)) {
                    return new AccountAt(Code.deserialize(account));
                }
    
                return null;
            }
            case "atall": {
                if (properties.isEmpty()) {
                    return AtAll.getInstance();
                } else {
                    return null;
                }
            }
            case "text": {
                final String text = properties.get("text");
                if (Objects.nonNull(text)) {
                    return new Text(text);
                } else {
                    return null;
                }
            }
            default:
                return null;
        }
    }
    
    private static MessageContent parseMessageContent(String type, Map<String, String> properties) {
        return null;
    }
    
    private static void report(String input, int index, String message) {
        final String exceptionMessage = "消息码编译错误：" + message + "（位于第 " + (index + 1) + " 个字符附近）\n" +
            input + "\n" +
            Strings.repeat(" ", Math.max(0, index - 2)) + "~~~";
        throw new IllegalArgumentException(exceptionMessage);
    }
    
    private static void check(boolean legal, String input, int index, String message) {
        if (!legal) {
            report(input, index, message);
        }
    }
}