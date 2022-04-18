package cn.codethink.xiaoming.util;

import cn.chuanwise.common.util.Preconditions;
import cn.chuanwise.common.util.StaticUtilities;

/**
 * 有关消息元素的字符串
 *
 * @author Chuanwise
 */
public class Texts
    extends StaticUtilities {

    /**
     * 转义字符串中的 {@link #TRANSLATED_CHARACTERS}
     *
     * @param text 需要转义的字符串
     * @return 转义后的字符串
     */
    @SuppressWarnings("all")
    public static String escape(String text) {
        Preconditions.objectNonNull(text, "text");
    
        // 如果是空字符串直接返回
        if (text.isEmpty()) {
            return text;
        }
    
        final int length = text.length();
        final StringBuilder stringBuilder = new StringBuilder(length + 1);
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
                case '\"':
                    stringBuilder.append("\\\"");
                    break;
                case '\'':
                    stringBuilder.append("\\\'");
                    break;
                case '[':
                    stringBuilder.append("\\[");
                    break;
                case ']':
                    stringBuilder.append("\\]");
                    break;
                case ',':
                    stringBuilder.append("\\,");
                    break;
                case ':':
                    stringBuilder.append("\\:");
                    break;
                case '=':
                    stringBuilder.append("\\=");
                    break;
                default:
                    stringBuilder.append(ch);
            }
        }
        
        return stringBuilder.toString();
    }
    
    /**
     * 反转义字符串中的特殊字符
     *
     * @param text 需要反转义的字符串
     * @return 反转义后的字符串
     */
    public static String unescape(String text) {
        Preconditions.objectNonNull(text, "text");
    
        // 如果是空字符串直接返回
        if (text.isEmpty()) {
            return text;
        }
        
        final int length = text.length();
        final StringBuilder stringBuilder = new StringBuilder(length - 1);
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
            
            stringBuilder.append(ch);
        }
        
        return stringBuilder.toString();
    }
}
