package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.property.Property;

import java.util.Map;
import java.util.function.Function;

/**
 * <h1>消息元数据类型</h1>
 *
 * <p>消息元数据是消息的属性，如消息的来源、引用的目标等。</p>
 *
 * <p>消息元数据类型表示消息元数据的类型，用于获取或设置消息元数据。</p>
 *
 * @param <T> 消息元数据内容类型
 */
@SuppressWarnings("all")
public interface MessageMetadataType<T extends MessageMetadata> {
    
    /**
     * 引用回复
     */
    Property<Quote> QUOTE = Property.newInstance();
    
    /**
     * 消息源
     */
    Property<MessageSource> SOURCE = Property.newInstance();
}