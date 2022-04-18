package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;

import java.util.List;

/**
 * <h1>转发消息构建器</h1>
 *
 * @author Chuanwise
 *
 * @see Forward
 * @see ForwardElement
 */
public interface ForwardBuilder {
    
    /**
     * 追加转发消息单元。
     *
     * @param element 转发消息单元
     * @return 转发消息构建器
     * @throws NullPointerException element 为 null
     */
    ForwardBuilder plus(ForwardElement element);
    
    /**
     * 追加消息转发单元，自动分配时间。
     * 如果存在上一条，则时间为上一条 + 1s，否则为当前时间戳。
     *
     * @param senderCode 发送人编号
     * @param senderName 发送人名
     * @param message 消息
     * @return 转发消息构建器
     * @throws NullPointerException senderCode, senderName 或 message 为 null
     * @throws IllegalArgumentException senderName 为 ""
     */
    ForwardBuilder plus(Code senderCode, String senderName, Message message);
    
    /**
     * 追加转发消息单元。
     *
     * @param elements 转发消息单元
     * @return 转发消息构建器
     * @throws NullPointerException elements 为 null
     */
    default ForwardBuilder plus(ForwardElement... elements) {
        Preconditions.objectNonNull(elements, "element");
        if (elements.length == 0) {
            return this;
        }
    
        for (ForwardElement element : elements) {
            plus(element);
        }
        
        return this;
    }
    
    /**
     * 追加转发消息单元。
     *
     * @param iterable 转发消息单元
     * @return 转发消息构建器
     * @throws NullPointerException iterable 为 null
     */
    default ForwardBuilder plus(Iterable<ForwardElement> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        for (ForwardElement forwardElement : iterable) {
            plus(forwardElement);
        }
        
        return this;
    }
    
    /**
     * 设置合并转发标题
     *
     * @param title 合并转发标题
     * @return 转发消息构建器
     * @throws NullPointerException title 为 null
     * @throws IllegalArgumentException title 为 ""
     * @see Forward#getTitle()
     */
    ForwardBuilder title(String title);
    
    /**
     * 设置合并转发描述
     *
     * @param description 合并转发描述
     * @return 转发消息构建器
     * @throws NullPointerException description 为 null
     * @throws IllegalArgumentException description 为 ""
     * @see Forward#getDescription()
     */
    ForwardBuilder description(String description);
    
    /**
     * 设置合并转发摘要
     *
     * @param summary 合并转发摘要
     * @return 转发消息构建器
     * @throws NullPointerException summary 为 null
     * @throws IllegalArgumentException summary 为 ""
     * @see Forward#getSummary()
     */
    ForwardBuilder summary(String summary);
    
    /**
     * 设置合并转发源头
     *
     * @param source 合并转发摘要
     * @return 转发消息构建器
     * @throws NullPointerException source 为 null
     * @throws IllegalArgumentException source 为 ""
     * @see Forward#getSource()
     */
    ForwardBuilder source(String source);
    
    /**
     * 设置合并转发预览
     *
     * @param preview 合并转发预览
     * @return 转发消息构建器
     * @throws NullPointerException preview 为 null
     * @throws IllegalArgumentException preview 为 ""
     * @see Forward#getPreview()
     */
    ForwardBuilder preview(List<String> preview);
    
    /**
     * 构建合并转发消息
     *
     * @return 合并转发消息
     */
    Forward build();
}
