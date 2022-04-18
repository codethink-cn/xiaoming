package cn.codethink.xiaoming.property;

import java.util.Map;

/**
 * 表示某种具有属性的对象
 *
 * @author Chuanwise
 */
public interface PropertyHolder {
    
    /**
     * 获取上下文的所有属性
     *
     * @return 上下文的所有属性
     */
    Map<Property<?>, Object> getProperties();
    
    /**
     * 获取上下文属性
     *
     * @param property 上下文属性类型
     * @param <T>      上下文属性值类型
     * @return 上下文属性值，或 null
     * @throws NullPointerException property 为 null
     */
    <T> T getProperty(Property<T> property);
    
    /**
     * 获取上下文属性
     *
     * @param property 上下文属性类型
     * @param <T>      上下文属性值类型
     * @return 上下文属性值
     * @throws NullPointerException             property 为 null
     * @throws java.util.NoSuchElementException 无此属性时
     */
    <T> T getPropertyOrFail(Property<T> property);
}
