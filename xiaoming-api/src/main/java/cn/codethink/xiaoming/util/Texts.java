package cn.codethink.xiaoming.util;

import cn.chuanwise.common.util.Collections;
import cn.chuanwise.common.util.Preconditions;
import cn.chuanwise.common.util.StaticUtilities;
import cn.chuanwise.common.util.Strings;

import java.util.Set;

/**
 * 有关消息元素的字符串
 *
 * @author Chuanwise
 */
public class Texts
    extends StaticUtilities {
    
    /**
     * 将被转义的字符集
     */
    public static final Set<Character> TRANSLATED_CHARACTERS = Collections.asUnmodifiableSet('[', ']', ',', ':', '=');
    
    /**
     * 转义字符
     */
    public static final char TRANSLATE_CHARACTER = '\\';
    
    /**
     * 转义字符串中的 {@link #TRANSLATED_CHARACTERS}
     *
     * @param text 需要转义的字符串
     * @return 转义后的字符串
     */
    public static String serializeText(String text) {
        Preconditions.objectNonNull(text, "text");
    
        // 如果是空字符串直接返回
        if (text.isEmpty()) {
            return text;
        }
    
        // 如果不包含任何转义字符，则不转义
        if (Strings.indexOfIncludedCharacter(text, TRANSLATED_CHARACTERS) == -1) {
            return text;
        }
        
        // 如果只有一个字符，直接转义
        final int length = text.length();
        if (length == 1) {
            return TRANSLATE_CHARACTER + text;
        }
        
        final StringBuilder stringBuilder = new StringBuilder(length + 1);
        for (int i = 0; i < length; i++) {
            final char ch = text.charAt(i);
    
            if (TRANSLATED_CHARACTERS.contains(ch)) {
                stringBuilder.append(TRANSLATE_CHARACTER);
            }
            
            stringBuilder.append(ch);
        }
        
        return stringBuilder.toString();
    }
    
    /**
     * 反转义字符串中的 {@link #TRANSLATED_CHARACTERS}
     *
     * @param text 需要反转义的字符串
     * @return 反转义后的字符串
     */
    public static String deserializeText(String text) {
        Preconditions.objectNonNull(text, "text");
    
        // 如果是空字符串直接返回
        if (text.isEmpty()) {
            return text;
        }
    
        // 如果不包含转义字符，则直接返回
        if (text.indexOf(TRANSLATE_CHARACTER) == -1) {
            return text;
        }
    
        final int length = text.length();
        final StringBuilder stringBuilder = new StringBuilder(length - 1);
        boolean translated = false;
        
        for (int i = 0; i < length; i++) {
            final char ch = text.charAt(i);
    
            if (translated) {
                stringBuilder.append(ch);
                translated = false;
                continue;
            }
            if (ch == TRANSLATE_CHARACTER) {
                translated = true;
                continue;
            }
            
            stringBuilder.append(ch);
        }
        
        return stringBuilder.toString();
    }
}
