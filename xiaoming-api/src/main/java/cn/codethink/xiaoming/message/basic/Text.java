package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;
import cn.codethink.xiaoming.message.Serializable;

import java.util.Objects;

/**
 * <h1>文本消息</h1>
 *
 * <p>表示一段普通的文本。</p>
 *
 * <p>序列化为消息码 {@link Serializable#serializeToMessageCode()} 时，文本消息中的 {@code \n \r \s \t} 等符号会被转义。</p>
 *
 * @author Chuanwise
 */
public interface Text
    extends BasicMessage, AutoSummarizable, AutoSerializable {
    
    /**
     * 一个空格文本消息
     */
    Text SPACE = of(" ");
    
    /**
     * 构建一个文本消息
     *
     * @param text 文本
     * @return 文本消息
     * @throws NullPointerException text 为 null
     * @throws IllegalArgumentException text 为 ""
     */
    // for ignore magic value warning
    @SuppressWarnings("all")
    static Text of(String text) {
        if (Objects.equals(text, " ") && Objects.nonNull(SPACE)) {
            return SPACE;
        } else {
            return XiaoMing.get().newText(text);
        }
    }
    
    /**
     * 构建一个文本消息
     *
     * @param charSequence 字符串
     * @return 文本消息
     * @throws NullPointerException text 为 null
     * @throws IllegalArgumentException text 为 ""
     */
    static Text of(CharSequence charSequence) {
        Preconditions.objectNonNull(charSequence, "char sequence");
        Preconditions.argument(charSequence.length() > 0, "char sequence is empty!");
        
        return of(charSequence.toString());
    }
    
    /**
     * 获取文本内容
     *
     * @return 文本内容
     */
    String getText();
}
