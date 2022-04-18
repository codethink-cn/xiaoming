package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.basic.Text;

/**
 * 一个可以被发送的消息
 *
 * @author Chuanwise
 */
public interface Message {
    
    /**
     * 在该消息结尾添加一个新的消息
     *
     * @param message 追加的消息
     * @return 连接形成的复合消息
     */
    CompoundMessage plus(Message message);
    
    /**
     * 在该消息结尾添加一些复合消息
     *
     * @param compoundMessage 追加的复合消息
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(CompoundMessage compoundMessage) {
        return plus((Iterable<? extends Message>) compoundMessage);
    }
    
    /**
     * 在该消息结尾添加一些新的文本
     *
     * @param text 追加的文本
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(CharSequence text) {
        return plus(Text.of(text));
    }
    
    /**
     * 在该消息结尾添加一些新的文本
     *
     * @param text 追加的文本
     * @return 连接形成的复合消息
     */
    CompoundMessage plus(String text);
    
    /**
     * 为来消息添加一个元数据
     *
     * @param messageMetadata 消息元数据
     * @return 连接形成的复合消息
     */
    CompoundMessage plus(MessageMetadata messageMetadata);
    
    /**
     * 在该消息结尾添加一些新的消息
     *
     * @param messages 追加的消息
     * @return 连接形成的复合消息
     */
    CompoundMessage plus(Message... messages);
    
    /**
     * 在该消息结尾添加一些新的消息
     *
     * @param iterable 追加的消息的迭代器
     * @return 连接形成的复合消息
     */
    CompoundMessage plus(Iterable<? extends Message> iterable);
    
    /**
     * 作为复合消息
     *
     * @return 复合消息
     */
    CompoundMessage asCompoundMessage();
}
