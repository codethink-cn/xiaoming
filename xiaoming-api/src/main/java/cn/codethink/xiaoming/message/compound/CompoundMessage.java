package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.api.Emptiable;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.Summarizable;
import cn.codethink.xiaoming.message.basic.BasicMessage;
import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.metadata.MessageMetadataType;
import cn.codethink.xiaoming.message.metadata.MessageSource;
import cn.codethink.xiaoming.message.metadata.OnlineMessageSource;
import cn.codethink.xiaoming.property.Property;

import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;

/**
 * <h1>复合消息</h1>
 *
 * <p>复合消息是多个基础消息 {@link BasicMessage} 前后连接在一起形成的链。用于表示一些较为复杂的消息内容。
 * 在各个通讯软件机器人平台上都能看到类似的设计，例如在 qq 机器人 qq 上，MessageChain 是复合消息。</p>
 *
 * <p>各个平台会实现自己的复合消息，当中会缓存一个平台相关的消息，如小明对 qq 机器人 qq 的支持核心中实现
 * 了 QqCompoundMessage，当中包含一个 MessageChain，便于再次发送消息。</p>
 *
 * <p>复合消息是不可变的，调用相关的 {@link #plus(CharSequence)} 等方法会返回一个新的复合消息，多次调用
 * 是低效的。建议使用消息构建器 {@link #}</p>
 *
 * @author Chuanwise
 */
public interface CompoundMessage
    extends Message, Iterable<BasicMessage>, Emptiable, Serializable, Summarizable, RandomAccess {
    
    /**
     * 获取复合消息长度
     *
     * @return 复合消息长度
     */
    int size();
    
    /**
     * 获取消息元数据
     *
     * @param type 消息元数据类型
     * @param <T>  元数据信息类型
     * @return 元数据信息
     */
    <T extends MessageMetadata> T getMetadata(Property<T> type);
    
    /**
     * 查询是否具备某一类型的消息元数据
     *
     * @param type 消息元数据类型
     * @return 元数据信息
     */
    boolean containsMetadata(Property<?> type);
    
    /**
     * 获取消息元数据
     *
     * @param type 消息元数据类型
     * @param <T>  元数据信息类型
     * @return 元数据信息
     * @throws java.util.NoSuchElementException 消息元数据不存在
     */
    <T extends MessageMetadata> T getMetadataOrFail(Property<T> type);
    
    /**
     * 获取所有消息元数据
     *
     * @return 所有消息元数据
     */
    Map<Property<?>, MessageMetadata> getMetadata();
    
    /**
     * 创建一个惰性消息构建器
     *
     * @return 复合消息构建器
     */
    default CompoundMessageBuilder asBuilder() {
        return CompoundMessageBuilder.lazy(this);
    }
    
    /**
     * 获取复合消息，即自身
     *
     * @return 自身
     */
    @Override
    default CompoundMessage asCompoundMessage() {
        return this;
    }
    
    /**
     * 获取指定位置处的基础消息
     *
     * @param index 索引
     * @return 基础消息
     * @throws IndexOutOfBoundsException index < 0 或 index >= {@link #size()}
     */
    BasicMessage get(int index);
    
    /**
     * 撤回消息
     *
     * @return 当消息不是在线消息，缺少权限，或已经被撤回，返回 false。其他情况返回 true
     * @throws cn.codethink.xiaoming.exception.PermissionDeniedException 缺少权限时
     */
    default boolean recall() {
        final MessageSource reference = getMetadata(MessageMetadataType.SOURCE);
        if (Objects.isNull(reference)) {
            return false;
        }
    
        if (!(reference instanceof OnlineMessageSource)) {
            return false;
        }
        final OnlineMessageSource onlineMessageSource = (OnlineMessageSource) reference;
    
        return onlineMessageSource.recall();
    }
}