package cn.codethink.xiaoming.property;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.spi.XiaoMing;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 表示某种属性
 *
 * @author Chuanwise
 */
public interface Property<T> {
    
    /**
     * 构造属性
     *
     * @param <T> 属性值
     * @return 属性
     */
    static <T> Property<T> newInstance() {
        return XiaoMing.get().newProperty();
    }
    
    /**
     * 构造属性
     *
     * @param getter 属性的获取方式
     * @param <T> 属性值
     * @return 属性
     * @throws NullPointerException getter 为 null
     */
    static <T> Property<T> newInstance(Function<Map<Property<?>, Object>, T> getter) {
        return XiaoMing.get().newProperty(getter);
    }
    
    /**
     * 表示会话
     */
    Property<Contact> CONTACT = newInstance();
    
    /**
     * 表示相关 bot
     */
    Property<Bot> BOT = newInstance(properties -> {
        final Contact contact = CONTACT.get(properties);
        if (Objects.nonNull(contact)) {
            return contact.getBot();
        } else {
            return null;
        }
    });
    
    /**
     * 表示会话或 bot，是 {@link #CONTACT} 和 {@link #BOT} 的复合属性
     */
    Property<ContactOrBot> CONTACT_OR_BOT = newInstance(properties -> {
        final Contact contact = CONTACT.get(properties);
        if (Objects.nonNull(contact)) {
            return contact;
        }
    
        return BOT.get(properties);
    });
    
    /**
     * 从属性表中获取属性
     *
     * @param properties 属性表
     * @return 属性值或 null
     * @throws NullPointerException properties 为 null
     */
    T get(Map<Property<?>, Object> properties);
    
    /**
     * 从具备某种属性的对象获取属性
     *
     * @param propertyHolder 具备属性的对象
     * @return 属性值或 null
     * @throws NullPointerException propertyHolder 为 null
     */
    T get(PropertyHolder propertyHolder);
    
    /**
     * 从属性表中获取属性
     *
     * @param properties 属性表
     * @return 属性值
     * @throws NullPointerException properties 为 null
     * @throws java.util.NoSuchElementException 找不到该属性
     */
    T getOrFail(Map<Property<?>, Object> properties);
    
    /**
     * 从具备某种属性的对象获取属性
     *
     * @param propertyHolder 具备属性的对象
     * @return 属性值
     * @throws NullPointerException propertyHolder 为 null
     * @throws java.util.NoSuchElementException 找不到该属性
     */
    T getOrFail(PropertyHolder propertyHolder);
}
