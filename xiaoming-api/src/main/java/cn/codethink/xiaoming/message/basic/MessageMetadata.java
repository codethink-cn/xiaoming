package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.metadata.MessageReference;
import cn.codethink.xiaoming.message.metadata.Quote;

/**
 * 元数据消息，如引用回复 {@link Quote}、消息源 {@link MessageReference} 等信息。
 * 消息元数据将存在于复合消息 {@link cn.codethink.xiaoming.message.compound.CompoundMessage} 中的元数据表中。
 *
 * 通过元数据类型 {@link MessageMetadataType} 获取消息元数据。
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public interface MessageMetadata
    extends Serializable {
    
    /**
     * 元数据类型
     *
     * @return 元数据类型
     */
    MessageMetadataType<?> getMetadataType();
}
