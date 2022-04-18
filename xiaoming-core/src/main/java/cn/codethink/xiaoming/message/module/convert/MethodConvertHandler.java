package cn.codethink.xiaoming.message.module.convert;

import cn.chuanwise.common.util.Exceptions;
import cn.chuanwise.common.util.Modifiers;
import cn.chuanwise.common.util.Preconditions;
import cn.chuanwise.common.util.Reflections;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.message.module.serialize.SerializeHandler;
import cn.codethink.xiaoming.message.module.summary.SummaryContext;
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
public class MethodConvertHandler
    implements ConvertHandler {
    
    private interface Wirer {
        
        /**
         * 填充一个参数
         *
         * @param context 转换上下文
         * @return 参数值
         */
        Object wire(ConvertContext context);
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
        public Object wire(ConvertContext context) {
            final Bot bot = context.getBot();
            Preconditions.operation(Objects.isNull(bot) || botClass.isInstance(bot), "supported bot class");
            return bot;
        }
    }
    
    @SuppressWarnings("all")
    private enum SourceWirer
        implements Wirer {
        
        INSTANCE;
        
        @Override
        public Object wire(ConvertContext context) {
            return context.getSource();
        }
    }
    
    @SuppressWarnings("all")
    private enum ContextWirer
        implements Wirer {
        
        INSTANCE;
        
        @Override
        public Object wire(ConvertContext context) {
            return context;
        }
    }
   
    @Data
    private static class ContactWirer
        implements Wirer {
        
        private final Class<? extends Contact> contactClass;
        
        @Override
        public Object wire(ConvertContext context) {
            final Contact contact = context.getPropertyOrFail(Property.CONTACT);
            Preconditions.operation(contactClass.isInstance(contact), "unsupported contact class");
            return contact;
        }
    }
    
    private final Object source;
    
    private final Method method;
    
    private final Class<?> sourceClass;
    
    private final Class<?>[] targetClasses;
    
    private final Wirer[] wirers;
    
    @SuppressWarnings("all")
    public MethodConvertHandler(Object invoker, Method method, Class<?> sourceClass, Class<?>[] targetClasses) {
        Preconditions.objectNonNull(method, "method");
        Preconditions.objectNonNull(sourceClass, "source class");
        Preconditions.objectNonNull(targetClasses, "target classes");
        
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
    
            if (ConvertContext.class.isAssignableFrom(parameterType)) {
                wirers[i] = ContextWirer.INSTANCE;
                continue;
            }
    
            if (Bot.class.isAssignableFrom(parameterType)) {
                wirers[i] = new BotWirer((Class<? extends Bot>) parameterType);
                continue;
            }
            
            if (Contact.class.isAssignableFrom(parameterType)) {
                wirers[i] = new ContactWirer((Class<? extends Contact>) parameterType);
                continue;
            }
    
            throw new IllegalArgumentException("the parameter " + i + " of convert method can only be subclass of " +
                sourceClass.getName() + ", " + ConvertContext.class.getName() + " or " + Bot.class.getName());
        }
        
        // check return type
        final Class<?> returnType = method.getReturnType();
        Preconditions.argument(!Objects.equals(returnType, void.class), "return type of deserializer method can not be void");

        if (targetClasses.length == 0) {
            this.targetClasses = new Class[] { returnType };
        } else {
            this.targetClasses = targetClasses;
    
            for (int i = 0; i < targetClasses.length; i++) {
                final Class<?> targetClass = targetClasses[i];
                Preconditions.argument(returnType.isAssignableFrom(targetClass), "target class " + i + " : " + targetClass.getName() +
                    " can not be assign to return type: " + returnType.getName());
            }
        }
    }
    
    @Override
    public Object convert(ConvertContext context) throws Exception {
        Preconditions.objectNonNull(context, "context");
    
        final Object[] arguments = new Object[wirers.length];
        for (int i = 0; i < wirers.length; i++) {
            arguments[i] = wirers[i].wire(context);
        }
    
        try {
            return Reflections.invokeMethod(source, method, arguments);
        } catch (InvocationTargetException e) {
            Exceptions.rethrow(e.getCause());
            return null;
        }
    }
}
