package cn.codethink.xiaoming.message.module.deserialize;

import cn.chuanwise.common.util.*;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.module.MessageModuleImpl;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.message.module.serialize.SerializeHandler;
import cn.codethink.xiaoming.util.MessageCodeTexts;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see SerializeHandler
 */
@Getter
public class MethodDeserializerHandler
    implements DeserializeHandler {
    
    private final List<DeserializerFormatElement> elements;
    
    private interface Wirer {
        
        /**
         * 填充一个参数
         *
         * @param context 反序列化上下文
         * @return 参数值
         */
        Object wire(DeserializeContext context);
    }
    
    @Data
    private static class ArgumentWirer
        implements Wirer {
        
        private final int index;
        
        @Override
        public Object wire(DeserializeContext context) {
            return context.getArguments().get(index);
        }
    }
    
    @Data
    private static class ArgumentDeserializerWirer
        implements Wirer {
    
        private final int index;
    
        @Override
        public Object wire(DeserializeContext context) {
            final String string = context.getArguments().get(index);
            return MessageModuleImpl.deserialize(new DeserializeContextImpl(MessageCodeTexts.parseArguments(string), context.getProperties()));
        }
    }
    
    @Data
    private static class RemainArgumentDeserializerWirer
        implements Wirer {
    
        private final int index;

        @Override
        public Object wire(DeserializeContext context) {
            final List<String> arguments = context.getArguments();
            final List<String> subList = arguments.subList(index, arguments.size());
            return MessageModuleImpl.deserialize(new DeserializeContextImpl(subList, context.getProperties()));
        }
    }
    
    @Data
    private static class RemainArgumentListWirer
        implements Wirer {
        
        private final int index;
    
        @Override
        public Object wire(DeserializeContext context) {
            final List<String> arguments = context.getArguments();
            final List<String> subList = arguments.subList(index, arguments.size());
            return subList;
        }
    }
    
    @Data
    private static class RemainArgumentWirer
        implements Wirer {
    
        private final int index;
    
        @Override
        public Object wire(DeserializeContext context) {
            final List<String> arguments = context.getArguments();
            final List<String> subList = arguments.subList(index, arguments.size());
            return Collections.toString(subList, ":");
        }
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
        public Object wire(DeserializeContext context) {
            final Bot bot = context.getBot();
            Preconditions.operation(Objects.isNull(bot) || botClass.isInstance(bot), "unsupported bot class");
            return bot;
        }
    }
    
    @SuppressWarnings("all")
    private enum ContextWirer
        implements Wirer {
        
        INSTANCE;
    
        @Override
        public Object wire(DeserializeContext context) {
            return context;
        }
    }
    
    @Data
    private static class ContactWirer
        implements Wirer {
        
        private final Class<? extends Contact> contactClass;
        
        @Override
        public Object wire(DeserializeContext context) {
            final Contact contact = context.getPropertyOrFail(Property.CONTACT);
            Preconditions.operation(contactClass.isInstance(contact), "unsupported contact class");
            return contact;
        }
    }
    
    private final Object source;
    
    private final Method method;
    
    private final Wirer[] wirers;
    
    @SuppressWarnings("all")
    public MethodDeserializerHandler(Object invoker, Method method, List<DeserializerFormatElement> elements) {
        Preconditions.objectNonNull(method, "method");
        Preconditions.objectNonNull(elements, "elements");
        
        this.method = method;
        this.elements = elements;
    
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
    
        int parameterCount = 0;
        final List<Integer> parameterIndexes = new ArrayList<>();
        final List<DeserializerFormatElement> parameterElements = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            final DeserializerFormatElement element = elements.get(i);
            if (element instanceof DeserializerFormatElement.PlainText) {
                continue;
            }
            
            parameterIndexes.add(i);
            parameterElements.add(element);
        }
        
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> parameterType = parameter.getType();
    
            final DeserializerValue deserializerValue = parameter.getAnnotation(DeserializerValue.class);
            if (Objects.nonNull(deserializerValue)) {
                final int index;
                
                final int annotationValue = deserializerValue.value();
                if (annotationValue == -1) {
                    Preconditions.objectIndex(parameterCount, parameterIndexes.size(), "parameter of " + method);
                    index = parameterIndexes.get(parameterCount);
                    parameterCount++;
                } else {
                    index = annotationValue;
    
                    if (elements.isEmpty()) {
                        throw new IndexOutOfBoundsException("parameter of " + method + " index " + index + " out of bound!");
                    } else {
                        final DeserializerFormatElement finalOne = elements.get(elements.size() - 1);
                        if (index >= elements.size() && !(finalOne instanceof DeserializerFormatElement.RemainParameter)) {
                            throw new IndexOutOfBoundsException("parameter of " + method + " index " + index + " out of bound!");
                        }
                    }
                }
                
                final DeserializerFormatElement element = elements.get(Math.min(elements.size() - 1, index));
                Preconditions.state(element instanceof DeserializerFormatElement.Parameter || element instanceof DeserializerFormatElement.RemainParameter);
    
                if (Objects.equals(parameterType, String.class)) {
                    if (element instanceof DeserializerFormatElement.RemainParameter) {
                        wirers[i] = new RemainArgumentWirer(index);
                    } else {
                        wirers[i] = new ArgumentWirer(index);
                    }
                } else {
                    if (element instanceof DeserializerFormatElement.RemainParameter) {
                        wirers[i] = new RemainArgumentDeserializerWirer(index);
                    } else {
                        wirers[i] = new ArgumentDeserializerWirer(index);
                    }
                }
                continue;
            }
    
            if (Bot.class.isAssignableFrom(parameterType)) {
                wirers[i] = new BotWirer((Class<? extends Bot>) parameterType);
                continue;
            }
            
            if (DeserializeContext.class.isAssignableFrom(parameterType)) {
                wirers[i] = ContextWirer.INSTANCE;
                continue;
            }
            
            if (Contact.class.isAssignableFrom(parameterType)) {
                wirers[i] = new ContactWirer((Class<? extends Contact>) parameterType);
                continue;
            }
            
            throw new IllegalArgumentException("the parameter " + i + " of deserializer method either has annotation " + DeserializerValue.class.getName() +
                " or is a class of " + Bot.class.getName() + " or " + DeserializeContext.class.getName());
        }
        
        // check return type
        final Class<?> returnType = method.getReturnType();
        Preconditions.argument(!Objects.equals(returnType, void.class), "return type of deserializer method can not be void");
    }
    
    @Override
    public Object invoke(DeserializeContext context) throws Exception {
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
