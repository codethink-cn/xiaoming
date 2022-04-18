package cn.codethink.xiaoming.message.module.summary;

import cn.chuanwise.common.util.*;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.module.serialize.SerializeHandler;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see SerializeHandler
 */
@Getter
@SuppressWarnings("all")
public class MethodSummaryHandler
    implements SummaryHandler {
    
    private interface Wirer {
        
        /**
         * 填充一个参数
         *
         * @param context 反序列化上下文
         * @return 参数值
         */
        Object wire(SummaryContext context);
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
        public Object wire(SummaryContext context) {
            final Bot bot = context.getBot();
            Preconditions.operation(Objects.isNull(bot) || botClass.isInstance(bot), "unsupported bot class");
            return bot;
        }
    }
    
    private enum SourceWirer
        implements Wirer {
        
        INSTANCE;
    
        @Override
        public Object wire(SummaryContext context) {
            return context.getSource();
        }
    }
    
    @SuppressWarnings("all")
    private enum ContextWirer
        implements Wirer {
        
        INSTANCE;
    
        @Override
        public Object wire(SummaryContext context) {
            return context;
        }
    }
    
    private final Object source;
    
    private final Method method;
    
    private final Wirer[] wirers;
    
    private final Class<? extends AutoSummarizable> sourceClass;
    
    @SuppressWarnings("all")
    public MethodSummaryHandler(Object invoker, Method method, Class<? extends AutoSummarizable> sourceClass) {
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

            if (SummaryContext.class.isAssignableFrom(parameterType)) {
                wirers[i] = ContextWirer.INSTANCE;
                continue;
            }
            
            if (Bot.class.isAssignableFrom(parameterType)) {
                wirers[i] = new BotWirer((Class<? extends Bot>) parameterType);
                continue;
            }
    
            throw new IllegalArgumentException("the parameter " + i + " of summarizer method can only be subclass of " +
                sourceClass.getName() + ", " + SummaryContext.class.getName() + " or " + Bot.class.getName());
        }
        
        // check return type
        final Class<?> returnType = method.getReturnType();
        Preconditions.argument(Objects.equals(returnType, String.class), "return type of summarizer method is " + String.class.getName());
    }
    
    @Override
    public String summary(SummaryContext context) throws Exception {
        Preconditions.objectNonNull(context, "context");
    
        final Object[] arguments = new Object[wirers.length];
        for (int i = 0; i < wirers.length; i++) {
            arguments[i] = wirers[i].wire(context);
        }
    
        try {
            return (String) Reflections.invokeMethod(source, method, arguments);
        } catch (InvocationTargetException e) {
            Exceptions.rethrow(e.getCause());
            return null;
        }
    }
}
