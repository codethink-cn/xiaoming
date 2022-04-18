package cn.codethink.xiaoming.message.module;

import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.basic.Text;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.spi.XiaoMing;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <h1>消息组件</h1>
 *
 * <p>消息组件是用于注册序列化器、进行序列化、注册转换器和进行转换的组件。</p>
 *
 * <h2>消息序列化器</h2>
 *
 * <p>用于自定义一些序列化器，实现自定义消息码的反序列化。当然也能注册和消息码无关的序列化器，但是没有必要这么做。</p>
 *
 * <p>序列化时，对象将会序列化为一个字符串数组；反序列化时，将把字符串数组反序列化为某种类型的对象。用于自定义消息码；例如，
 * {@link cn.codethink.xiaoming.message.basic.ResourceImage} 资源图片中涉及的外部资源
 * {@link cn.codethink.xiaoming.resource.Resource} 可以序列化为字符串数组存入图片的消息码中。</p>
 *
 * <p>特别地，对于 {@link cn.codethink.xiaoming.message.basic.Text}，直接调用 {@link Text#serializeToMessageCode()}
 * 将获得比 {@link MessageModule#serialize(Object, Map)} 更简明的结果。</p>
 *
 * <h2>消息转换器</h2>
 *
 * <p>用于将小明可序列化对象转换为其他某种平台的对象。</p>
 *
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.Serializable
 * @see Deserializer
 */
public interface MessageModule {
    
    /**
     * 将参数列表反序列化为对象
     *
     * @param arguments 参数列表
     * @param properties 相关属性
     * @return 对象
     * @throws NullPointerException     arguments 或 properties 为 null
     * @throws IllegalArgumentException arguments 为空或无法反序列化该参数
     * @see Deserializer
     */
    static Object deserialize(List<String> arguments, Map<Property<?>, Object> properties) {
        return XiaoMing.get().deserialize(arguments, properties);
    }
    
    /**
     * 将参数列表反序列化为对象
     *
     * @param arguments 参数列表
     * @return 对象
     * @throws NullPointerException     arguments 为 null
     * @throws IllegalArgumentException arguments 为空或无法反序列化该参数
     * @see Deserializer
     */
    static Object deserialize(List<String> arguments) {
        return XiaoMing.get().deserialize(arguments, Collections.emptyMap());
    }
    
    /**
     * 将对象序列化为参数列表
     *
     * @param source 对象
     * @param properties 相关属性
     * @return 参数列表
     * @throws NullPointerException     source 或 properties 为 null
     * @throws IllegalArgumentException 没有合适的序列化器
     * @see cn.codethink.xiaoming.message.module.serialize.Serializer
     */
    static List<String> serialize(Object source, Map<Property<?>, Object> properties) {
        return XiaoMing.get().serialize(source, properties);
    }
    
    /**
     * 将对象序列化为参数列表
     *
     * @param source 对象
     * @return 参数列表
     * @throws NullPointerException     source 为 null
     * @throws IllegalArgumentException 没有合适的序列化器
     * @see cn.codethink.xiaoming.message.module.serialize.Serializer
     */
    static List<String> serialize(Object source) {
        return XiaoMing.get().serialize(source, Collections.emptyMap());
    }
    
    /**
     * 转换某一类型的消息到另一种类型。
     *
     * @param source     源对象
     * @param properties 相关属性
     * @return 目标类型
     * @throws NullPointerException     source 或 properties 为 null 时
     * @throws IllegalArgumentException 没有合适的转换器
     */
    static Object convert(Object source, Map<Property<?>, Object> properties) {
        return XiaoMing.get().convert(source, Object.class, properties);
    }
    
    /**
     * 转换某一类型的消息到另一种类型。
     *
     * @param source 源对象
     * @return 目标类型
     * @throws NullPointerException     source 为 null 时
     * @throws IllegalArgumentException 没有合适的转换器
     */
    static Object convert(Object source) {
        return convert(source, Collections.emptyMap());
    }
    
    /**
     * 转换某一类型的消息到另一种类型。
     *
     * @param source     源对象
     * @param properties 相关属性
     * @param targetClass 目标类型
     * @return 目标类型
     * @throws NullPointerException     source, targetClass 或 properties 为 null 时
     * @throws IllegalArgumentException 没有合适的转换器
     */
    static <T> T convert(Object source, Class<T> targetClass, Map<Property<?>, Object> properties) {
        return XiaoMing.get().convert(source, targetClass, properties);
    }
    
    /**
     * 转换某一类型的消息到另一种类型。
     *
     * @param source 源对象
     * @param targetClass 目标类型
     * @return 目标类型
     * @throws NullPointerException     source, targetClass 或 properties 为 null 时
     * @throws IllegalArgumentException 没有合适的转换器
     */
    static <T> T convert(Object source, Class<T> targetClass) {
        return convert(source, targetClass, Collections.emptyMap());
    }
    
    /**
     * 获取对象的摘要
     *
     * @param source 对象
     * @param properties 相关属性
     * @return 摘要信息
     * @throws NullPointerException     source 或 properties 为 null
     * @throws IllegalArgumentException 没有合适的摘要器
     * @throws NullPointerException     方法并不会检查 bot 是否为空。只有在摘要需要 bot，bot 却为 null 时才会抛出该异常。
     */
    static String summary(AutoSummarizable source, Map<Property<?>, Object> properties) {
        return XiaoMing.get().summary(source, properties);
    }
    
    /**
     * 获取对象的摘要
     *
     * @param source 对象
     * @return 摘要信息
     * @throws NullPointerException     source 为 null
     * @throws IllegalArgumentException 没有合适的摘要器
     * @throws NullPointerException     方法并不会检查 bot 是否为空。只有在摘要需要 bot，bot 却为 null 时才会抛出该异常。
     */
    static String summary(AutoSummarizable source) {
        return XiaoMing.get().summary(source, Collections.emptyMap());
    }
    
    /**
     * 注册消息组件
     *
     * @param module 消息组件
     * @throws NullPointerException module 为 null
     */
    static void registerModule(Object module) {
        XiaoMing.get().registerMessageModule(module);
    }
    
    /**
     * 注销消息组件
     *
     * @param module 消息组件
     * @return 是否成功注销
     * @throws NullPointerException module 为 null
     */
    static boolean unregisterModule(Object module) {
        return XiaoMing.get().unregisterMessageModule(module);
    }
    
    /**
     * 注销某类型的消息组件
     *
     * @param moduleClass 消息组件类型
     * @return 是否成功注销
     * @throws NullPointerException moduleClass 为 null
     */
    static boolean unregisterModule(Class<?> moduleClass) {
        return XiaoMing.get().unregisterMessageModule(moduleClass);
    }
}