package cn.codethink.xiaoming.message.module.serialize;

import cn.chuanwise.common.util.*;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.message.module.summary.MethodSummaryHandler;
import cn.codethink.xiaoming.message.module.summary.SummaryContext;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see SerializeHandler
 */
@Getter
public class MethodSerializeHandler
    implements SerializeHandler {
    
    private interface Wirer {
        
        /**
         * 填充一个参数
         *
         * @param context 反序列化上下文
         * @return 参数值
         */
        Object wire(SerializeContext context);
    }
    
    @Data
    private static class BotWirer
        implements Wirer {
        
        private final Class<? extends Bot> botClass;
        
        public BotWirer(Class<? extends Bot> botClass) {
            Preconditions.objectNonNull(botClass, "bot class");
            
            this.botClass = botClass;
        }
        
        @Override
        public Object wire(SerializeContext context) {
            final Bot bot = context.getBot();
            Preconditions.operation(Objects.isNull(bot) || botClass.isInstance(bot), "unsupported bot class");
            return bot;
        }
    }
    
    @SuppressWarnings("all")
    private enum SourceWirer
        implements Wirer {
        
        INSTANCE;
        
        @Override
        public Object wire(SerializeContext context) {
            return context.getSource();
        }
    }
    
    @SuppressWarnings("all")
    private enum ContextWirer
        implements Wirer {
        
        INSTANCE;
        
        @Override
        public Object wire(SerializeContext context) {
            return context;
        }
    }
    
    private final Object source;
    
    private final Method method;
    
    private final boolean returnList;
    
    private final Class<?> sourceClass;
    
    private final Wirer[] wirers;
    
    @SuppressWarnings("all")
    public MethodSerializeHandler(Object invoker, Method method, Class<?> sourceClass) {
        Preconditions.objectNonNull(method, "method");
        Preconditions.objectNonNull(sourceClass, "source class");
        
        this.method = method;
        this.sourceClass = sourceClass;
        
        // check invoker
        final Class<?> declaringClass = method.getDeclaringClass();
        if (Objects.isNull(invoker) || Objects.equals(invoker, declaringClass)) {
            Preconditions.argument(Modifiers.isStatic(method), "invoker object is null, but method is not static");
            this.source = declaringClass;
        } else {
            Preconditions.argument(declaringClass.isInstance(invoker), "invoker object is not null, and method is not static, " +
                "but invoker object is not an instance of method declaring class: " + declaringClass.getName());
            this.source = invoker;
        }
        
        // check argument
        final Parameter[] parameters = method.getParameters();
        wirers = new Wirer[parameters.length];
    
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> parameterType = parameter.getType();
        
            if (sourceClass.isAssignableFrom(parameterType)) {
                wirers[i] = SourceWirer.INSTANCE;
                continue;
            }
        
            if (SerializeContext.class.isAssignableFrom(parameterType)) {
                wirers[i] = ContextWirer.INSTANCE;
                continue;
            }
        
            if (Bot.class.isAssignableFrom(parameterType)) {
                wirers[i] = new BotWirer((Class<? extends Bot>) parameterType);
                continue;
            }
        
            throw new IllegalArgumentException("the parameter " + i + " of summarizer " + method + " can only be subclass of " +
                sourceClass.getName() + ", " + SerializeContext.class.getName() + " or " + Bot.class.getName());
        }
        // check return type
        final Class<?> returnType = method.getReturnType();
        if (List.class.isAssignableFrom(returnType)) {
            Preconditions.argument(Objects.equals(String.class, Types.getTypeParameterClass(method.getGenericReturnType(), List.class)),
                "return type of serializer " + method + " can only be String[] or List<String>");
            returnList = true;
        } else {
            Preconditions.argument(Objects.equals(returnType, String[].class), "return type of serializer " + method + " can only be String[] or List<String>");
            returnList = false;
        }
    }
    
    @Override
    @SuppressWarnings("all")
    public List<String> serialize(SerializeContext context) throws Exception {
        Preconditions.objectNonNull(context, "context");
    
        try {
            final Object[] arguments = new Object[wirers.length];
            for (int i = 0; i < wirers.length; i++) {
                arguments[i] = wirers[i].wire(context);
            }
            
            final Object returnValue = Reflections.invokeMethod(source, method, arguments);
            if (returnList) {
                return (List<String>) returnValue;
            } else {
                return Arrays.asUnmodifiableList((String[]) returnValue);
            }
        } catch (InvocationTargetException e) {
            Exceptions.rethrow(e.getCause());
            return null;
        }
    }
}