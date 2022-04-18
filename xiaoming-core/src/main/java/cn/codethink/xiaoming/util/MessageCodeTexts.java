package cn.codethink.xiaoming.util;

import cn.chuanwise.common.util.Preconditions;
import cn.chuanwise.common.util.StaticUtilities;
import cn.codethink.xiaoming.message.basic.ResourceImage;
import cn.codethink.xiaoming.message.basic.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 消息码相关字符转义工具
 *
 * @author Chuanwise
 */
public class MessageCodeTexts
    extends StaticUtilities {
    
    /**
     * 将参数转化为可以作为基础消息参数的值。
     * 将会转义其中的空白符，以及 : 和 ]。
     *
     * @param argument 参数
     * @return 转义后的参数字符串
     */
    public static String toBasicMessageArgument(Object argument) {
        final String text = Objects.toString(argument.toString());
        Preconditions.objectNonNull(text, "text");
    
        if (text.isEmpty()) {
            return text;
        }
    
        final int length = text.length();
        final StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final char ch = text.charAt(i);
    
            switch (ch) {
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case ']':
                    stringBuilder.append("\\]");
                    break;
                case ':':
                    stringBuilder.append("\\:");
                    break;
                default:
                    stringBuilder.append(ch);
            }
        }
        
        return stringBuilder.toString();
    }
    
    /**
     * 将基础消息参数的值反序列化为原文字符串。
     * 将会转义其中的空白符，以及 \: 和 \]
     *
     * @param text 转义后的参数字符串
     * @return 原文字符串
     * @throws NullPointerException text 为 null
     */
    public static String parseBasicMessageArgument(String text) {
        Preconditions.objectNonNull(text, "text");
    
        if (text.isEmpty()) {
            return text;
        }
        
        final int length = text.length();
        final StringBuilder stringBuilder = new StringBuilder(length);
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            final char ch = text.charAt(i);
    
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
            if (ch == '\\') {
                escaped = true;
                continue;
            }
    
            stringBuilder.append(ch);
        }
        
        return stringBuilder.toString();
    }
    
    /**
     * 将字符串转义为可以直接放在消息链中的字符串。
     * 将会转义其中的空白符，以及 [。
     *
     * @param text 转义前的原文
     * @return 转移后的消息链字符串
     * @throws NullPointerException text 为 null
     */
    public static String toTextMessageCode(String text) {
        Preconditions.objectNonNull(text, "text");
    
        if (text.isEmpty()) {
            return text;
        }
    
        final int length = text.length();
        final StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final char ch = text.charAt(i);
        
            switch (ch) {
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case '[':
                    stringBuilder.append("\\[");
                    break;
                default:
                    stringBuilder.append(ch);
            }
        }
    
        return stringBuilder.toString();
    }
    
    /**
     * 将字符串转义为可以直接放在消息链中的字符串。
     * 将会转义其中的空白符，以及 [。
     *
     * @param text 转义前的原文
     * @return 转移后的消息链字符串
     */
    public static String parseTextMessageCode(String text) {
        return parseBasicMessageArgument(text);
    }
    
    public static List<String> parseArguments(String text) {
        Preconditions.objectNonNull(text, "text");
    
        final int length = text.length();
        if (length == 0) {
            return Collections.emptyList();
        }
        
        final StringBuilder stringBuilder = new StringBuilder();
        final List<String> list = new ArrayList<>();
        
        boolean escaped = false;
    
        for (int i = 0; i < length; i++) {
            final char ch = text.charAt(i);
    
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
            if (ch == '\\') {
                escaped = true;
                continue;
            }
            
            if (ch == ':') {
                final String string = stringBuilder.toString();
                stringBuilder.setLength(0);
                list.add(string);
            } else {
                stringBuilder.append(ch);
            }
        }
    
        if (stringBuilder.length() > 0) {
            final String string = stringBuilder.toString();
            list.add(string);
        }
        
        return Collections.unmodifiableList(list);
    }
    
    public static String serializeComma(String string) {
        if (!string.contains(",")) {
            return string;
        }
    
        final int length = string.length();
        final StringBuilder stringBuilder = new StringBuilder(length);
    
        for (int i = 0; i < length; i++) {
            final char ch = string.charAt(i);
    
            if (ch == ',') {
                stringBuilder.append('\\');
            }
            stringBuilder.append(ch);
        }
        
        return stringBuilder.toString();
    }
    
    public static List<String> deserializeComma(String string) {
        final int length = string.length();
        final List<String> list = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder(length);
    
        for (int i = 0; i < length; i++) {
            final char ch = string.charAt(i);
        
            if (ch == ',') {
                final String element = stringBuilder.toString();
                stringBuilder.setLength(0);
                list.add(element);
            } else {
                stringBuilder.append(ch);
            }
        }
        
        if (stringBuilder.length() > 0) {
            final String element = stringBuilder.toString();
            list.add(element);
        }
    
        return list;
    
    }
}