package cn.codethink.xiaoming.message.content;

import cn.codethink.xiaoming.message.content.SimpleMessageContentBuilder;
import cn.codethink.xiaoming.message.element.MessageElement;
import cn.codethink.xiaoming.message.element.Text;
import cn.codethink.util.Preconditions;

/**
 * 可以用来构造消息正文的，可以调用 plus 之类的方法
 *
 * @author Chuanwise
 */
public interface MessageContentBuildable {
    
    /**
     * 以该消息开头构造一个消息内容构造器
     *
     * @return 消息内容构造器
     */
    cn.codethink.xiaoming.message.content.SimpleMessageContentBuilder asMessageContentBuilder();
    
    /**
     * 添加一个消息元素
     *
     * @param messageElement 消息元素
     * @return 消息内容构造器
     */
    cn.codethink.xiaoming.message.content.SimpleMessageContentBuilder plus(MessageElement messageElement);
    
    /**
     * 添加一个文本消息元素
     *
     * @param text 文本消息元素
     * @return 消息内容构造器
     */
    default cn.codethink.xiaoming.message.content.SimpleMessageContentBuilder plus(String text) {
        Preconditions.namedArgumentNonEmpty(text, "text");
        
        return plus(new Text(text));
    }
    
    /**
     * 添加集合中的所有消息元素
     *
     * @param messageElements 消息元素
     * @return 消息内容构造器
     */
    SimpleMessageContentBuilder plusAll(Iterable<MessageElement> messageElements);
}
