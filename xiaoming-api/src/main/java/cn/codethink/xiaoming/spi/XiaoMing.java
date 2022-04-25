package cn.codethink.xiaoming.spi;

import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.message.metadata.MessageSource;
import cn.codethink.xiaoming.message.metadata.Quote;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.resource.Resource;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 小明核心，是小明系统的核心 API。
 * 启动时，由小明的内核通过 {@link XiaoMingSpi#setXiaoMing(XiaoMing)} 注册。
 *
 * @author Chuanwise
 */
@InternalAPI
public interface XiaoMing {
    
    /**
     * 反序列化消息码为消息体
     *
     * @param messageCode 消息码
     * @param properties  相关属性
     * @return 消息体
     */
    CompoundMessage deserializeMessageCode(String messageCode, Map<Property<?>, Object> properties);
    
    /**
     * 获得一个代表 long 值的 Code
     *
     * @param code 值
     * @return 码
     */
    Code getCode(long code);
    
    /**
     * 获得一个代表 int 值的 Code
     *
     * @param value 值
     * @return 码
     */
    Code getCode(int value);
    
    /**
     * 获得一个代表字符串的 Code
     *
     * @param value 值
     * @return 码
     * @throws NullPointerException value 为 null
     */
    Code getCode(String value);
    
    /**
     * 反序列化标识码
     *
     * @param string 标识码字符串
     * @return 标识码
     * @throws NullPointerException string 为 null
     * @throws IllegalArgumentException string 格式错误
     */
    Code parseCode(String string);
    
    /**
     * 由资源创建一个资源图片
     *
     * @param resource 资源
     * @return 资源图片
     * @throws NullPointerException resource 为 null
     */
    ResourceImage newResourceImage(Resource resource);
    
    /**
     * 将外部文件作为资源
     *
     * @param file 外部文件
     * @return 资源
     * @throws NullPointerException file 为 null
     */
    Resource newResource(File file);
    
    /**
     * 将类路径下的组件作为资源
     *
     * @param clazz 类
     * @param path  路径
     * @return 资源
     * @throws NullPointerException     clazz 或 path 为 null
     * @throws IllegalArgumentException path 为 ""
     */
    Resource newResource(Class<?> clazz, String path);
    
    
    /**
     * 用 url 创建资源
     *
     * @param url 外部文件
     * @return 资源
     * @throws NullPointerException url 为 null
     */
    Resource newResource(URL url);
    
    /**
     * 将字节数组作为资源
     *
     * @param bytes 字节数组
     * @return 资源
     * @throws NullPointerException bytes 为 null
     */
    Resource newResource(byte[] bytes);
    
    /**
     * 将图片包装为闪照
     *
     * @param image 图片
     * @return 闪照
     * @throws NullPointerException image 为 null
     */
    FlashImage newFlashImage(Image image);
    
    /**
     * 将图片包装为自定义表情
     *
     * @param image 图片
     * @return 自定义表情
     * @throws NullPointerException image 为 null
     */
    CustomFace newCustomFace(Image image);
    
    /**
     * 获取小明核心
     *
     * @return 小明核心
     * @throws java.util.NoSuchElementException 缺少小明核心
     */
    static XiaoMing get() {
        return XiaoMingSpi.getXiaoMing();
    }
    
    /**
     * 创建图片类型
     *
     * @param extension 扩展名
     * @return 图片类型
     * @throws NullPointerException     extension 为 null
     * @throws IllegalArgumentException extension 已被创建或为 ""
     */
    ImageCodec newImageType(String extension);
    
    /**
     * 创建一个新的消息构建器
     *
     * @return 消息构建器
     */
    CompoundMessageBuilder newCompoundMessageBuilder();
    
    /**
     * 创建一个新的消息构建器
     *
     * @return 消息构建器
     */
    CompoundMessage newCompoundMessage(BasicMessage basicMessage);
    
    /**
     * 通过复制现有的复合消息创建消息构建器
     *
     * @param compoundMessage 复合消息
     * @return 消息构建器
     */
    CompoundMessageBuilder copyAsCompoundMessageBuilder(CompoundMessage compoundMessage);
    
    /**
     * 通过现有的复合消息，创建惰性消息构建器。
     *
     * @param compoundMessage 复合消息
     * @return 消息构建器
     * @throws NullPointerException compoundMessage 为 null
     */
    CompoundMessageBuilder newLazyCompoundMessageBuilder(CompoundMessage compoundMessage);
    
    /**
     * 创建一个新的消息构建器，并预留一定的大小
     *
     * @param capacity 预留大小
     * @return 消息构建器
     */
    CompoundMessageBuilder newCompoundMessageBuilder(int capacity);
    
    /**
     * 通过扩展名获取图片类型
     *
     * @param extension 扩展名
     * @return 图片类型
     * @throws NullPointerException             extension 为 null
     * @throws IllegalArgumentException         extension 为 ""
     * @throws java.util.NoSuchElementException 没有找到图片类型
     */
    ImageCodec getImageType(String extension);
    
    /**
     * 获取全部图片类型
     *
     * @return 图片类型
     */
    Set<ImageCodec> getImageTypes();
    
    /**
     * 获取提及所有成员消息
     *
     * @return 提及所有成员消息
     */
    AtAll getAllAccountAt();
    
    /**
     * 构建一个文本消息
     *
     * @param text 文本
     * @return 文本消息
     * @throws NullPointerException     text 为 null
     * @throws IllegalArgumentException text 为 ""
     */
    Text newText(String text);
    
    /**
     * 创建转发消息构建器
     *
     * @return 转发消息构建器
     */
    ForwardBuilder newForwardBuilder();
    
    /**
     * 构造指向消息源的回复信息
     *
     * @param source 消息源
     * @return 引用回复
     * @throws NullPointerException source 为 null
     */
    Quote newQuote(MessageSource source);
    
    /**
     * 构造属性
     *
     * @param <T> 属性值
     * @return 属性
     */
    <T> Property<T> newProperty();
    
    /**
     * 构造属性
     *
     * @param getter 属性的获取方式
     * @param <T> 属性值
     * @return 属性
     * @throws NullPointerException getter 为 null
     */
    <T> Property<T> newProperty(Function<Map<Property<?>, Object>, T> getter);
    
    /**
     * 构造转发消息单元
     *
     * @param senderCode 发送者账户号
     * @param senderName 发送者名
     * @param timestamp  时间戳
     * @param message    消息
     * @return 转发消息单元
     * @throws NullPointerException     senderCode, senderName 或 message 为 null
     * @throws IllegalArgumentException senderName 为 ""
     */
    ForwardElement newForwardElement(Code senderCode, String senderName, long timestamp, Message message);
    
    /**
     * 构造提及单人账号消息
     *
     * @param targetCode 账号码
     * @return 提及单人账号消息
     * @throws NullPointerException targetCode 为 null
     */
    SingletonAt newSingletonAccountAt(Code targetCode);
    
    /**
     * 构造一个音乐分享消息
     *
     * @param softwareType 音乐软件类型
     * @param title        标题
     * @param description  描述
     * @param summary      摘要
     * @param jumpUrl      跳转 Url
     * @param coverUrl     封面 Url
     * @param musicUrl     音乐 Url
     * @return 音乐分享消息
     * @throws NullPointerException     softwareType, title, description, summary, jumpUrl, coverUrl 或 musicUrl 为 null
     * @throws IllegalArgumentException title, description, summary, jumpUrl, coverUrl 或 musicUrl 为 ""
     */
    MusicShare newMusicShare(MusicSoftwareType softwareType,
                             String title,
                             String description,
                             String summary,
                             String jumpUrl,
                             String coverUrl,
                             String musicUrl);
    
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
    String summary(AutoSummarizable source, Map<Property<?>, Object> properties);
    
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
    <T> T convert(Object source, Class<T> targetClass, Map<Property<?>, Object> properties);
    
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
    List<String> serialize(Object source, Map<Property<?>, Object> properties);
    
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
    Object deserialize(List<String> arguments, Map<Property<?>, Object> properties);
    
    /**
     * 注册消息组件
     *
     * @param module 消息组件
     * @throws NullPointerException module 为 null
     */
    void registerMessageModule(Object module);
    
    /**
     * 注销消息组件
     *
     * @param module 消息组件
     * @return 是否成功注销
     * @throws NullPointerException module 为 null
     */
    boolean unregisterMessageModule(Object module);
    
    /**
     * 注销某类型的消息组件
     *
     * @param moduleClass 消息组件类型
     * @return 是否成功注销
     * @throws NullPointerException moduleClass 为 null
     */
    boolean unregisterMessageModule(Class<?> moduleClass);
    
    /**
     * 注册时执行的方法
     */
    void onRegister();
    
    /**
     * 卸载时执行的方法
     */
    void onDeregister();
}