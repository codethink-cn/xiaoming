package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.property.Property;

import java.util.Map;

/**
 * <h1>可自动摘要对象</h1>
 *
 * <p>该接口是为已注册摘要组件的类型编写的，自动调用 {@link MessageModule#serialize(Object, Map)} 获取消息摘要。</p>
 *
 * <p>请确保在调用 {@link #serializeToMessageSummary(Map)} 前，已经通过 {@link MessageModule#registerModule(Object)}
 * 注册了摘要器。否则该方法将抛出 {@link IllegalArgumentException}异常。</p>
 *
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.Summarizable
 * @see cn.codethink.xiaoming.message.module.summary.Summarizer
 */
public interface AutoSummarizable
    extends Summarizable {
    
    /**
     * 按照注册的消息组件序列化为消息摘要
     *
     * @param properties 相关属性
     * @return 描述消息内容的字符串
     * @throws NullPointerException     需要 contact 但 contact 为 null 时
     * @throws IllegalArgumentException 没有合适的摘要器
     */
    @Override
    default String serializeToMessageSummary(Map<Property<?>, Object> properties) {
        return MessageModule.summary(this, properties);
    }
}