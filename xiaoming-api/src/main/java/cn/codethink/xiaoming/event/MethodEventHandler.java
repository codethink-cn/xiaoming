package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.Reflections;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.annotation.InternalAPI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * 方法事件监听器
 *
 * @author Chuanwise
 */
@InternalAPI
public class MethodEventHandler
    extends AbstractEventHandler {
    
    protected final Object source;
    
    protected final Method method;
    
    private MethodEventHandler(Class<?> eventClass, Object source, Method method, Priority priority, boolean alwaysValid) {
        super(eventClass, priority, alwaysValid);
    
        Preconditions.nonNull(source, "source");
        Preconditions.nonNull(method, "method");
        Preconditions.nonNull(priority, "priority");
        
        this.source = source;
        this.method = method;
    }
    
    /**
     * 用方法构造一个事件处理器
     *
     * @param source               方法所属类的对象
     * @param method               方法
     * @param alwaysValid 是否监听已经被取消的事件
     * @return 事件处理器
     */
    @InternalAPI
    @SuppressWarnings("all")
    public static MethodEventHandler ofMethod(Object source, Method method, Priority priority, boolean alwaysValid) {
        Preconditions.nonNull(source, "source");
        Preconditions.nonNull(method, "method");
        Preconditions.nonNull(priority, "priority");
        
        // 检查 Method 是否是 source 类的方法
        final Class<?> methodClass = method.getDeclaringClass();
        final int modifiers = method.getModifiers();
        
        // 只有 source 是 Class<?> 且 method 是 static
        // 或 source 是方法定义类的实例时才能调用
        Preconditions.argument((Modifier.isStatic(modifiers) && Objects.equals(source, methodClass))
                || methodClass.isInstance(source),
            "method should be static and source object equals to the declaring class of method, " +
                "or the source object must be a instance of the declaring class of method.");
    
        // 判断参数个数
        final Parameter[] parameters = method.getParameters();
        Preconditions.argument(parameters.length == 1, "argument count of a method event handler must be equals to 0!");
        final Parameter parameter = parameters[0];
    
        // 构造并返回
        final Class<?> eventClass = parameter.getType();
        return new MethodEventHandler((Class<? extends Event>) eventClass, source, method, priority, alwaysValid);
    }
    
    /**
     * 用静态方法构造一个事件处理器
     *
     * @param method      方法
     * @param alwaysValid 是否监听已经被取消的事件
     * @return 事件处理器
     */
    @InternalAPI
    public static MethodEventHandler ofStaticMethod(Method method, Priority priority, boolean alwaysValid) {
        Preconditions.nonNull(method, "method");
        
        return ofMethod(method.getDeclaringClass(), method, priority, alwaysValid);
    }
    
    @Override
    protected boolean handleEvent0(Event event) throws Exception {
        // invoke method
        try {
            Reflections.invokeMethod(source, method, event);
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw (Exception) cause;
            }
        }
    
        return true;
    }
}