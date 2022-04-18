package cn.codethink.xiaoming.message.module;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.module.convert.ConvertContext;
import cn.codethink.xiaoming.message.module.convert.Convertor;
import cn.codethink.xiaoming.message.module.convert.MethodConvertHandler;
import cn.codethink.xiaoming.message.module.deserialize.*;
import cn.codethink.xiaoming.message.module.serialize.MethodSerializeHandler;
import cn.codethink.xiaoming.message.module.serialize.SerializeContext;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.MethodSummaryHandler;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.message.module.summary.SummaryContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author Chuanwise
 *
 * @see MessageModule
 */
public class MessageModuleImpl
    implements MessageModule {
    
    private static final Map<Priority, List<MethodConvertHandler>> CONVERTORS = new HashMap<>();
    
    private static final Map<Priority, List<MethodSerializeHandler>> SERIALIZERS = new HashMap<>();
    
    private static final Map<Priority, List<MethodDeserializerHandler>> DESERIALIZERS = new HashMap<>();
    
    private static final Map<Priority, List<MethodSummaryHandler>> SUMMARIZERS = new HashMap<>();
    
    public static Object convert(ConvertContext context) {
        Preconditions.objectNonNull(context, "context");
        
        try {
            Object object = convert(Priority.HIGHEST, context);
            if (Objects.nonNull(object)) {
                return object;
            }
    
            object = convert(Priority.HIGH, context);
            if (Objects.nonNull(object)) {
                return object;
            }
    
            object = convert(Priority.NORMAL, context);
            if (Objects.nonNull(object)) {
                return object;
            }
    
            object = convert(Priority.LOW, context);
            if (Objects.nonNull(object)) {
                return object;
            }
    
            object = convert(Priority.LOWEST, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("convert failed for " + context.getSource(), exception);
        }
    
        throw new IllegalArgumentException("no convertor present for " + context.getSource());
    }
    
    private static Object convert(Priority priority, ConvertContext context) throws Exception {
        final List<MethodConvertHandler> convertors = CONVERTORS.get(priority);
        final Object source = context.getSource();
    
        if (cn.chuanwise.common.util.Collections.nonEmpty(convertors)) {
            for (MethodConvertHandler convertor : convertors) {
                
                if (!convertor.getSourceClass().isInstance(source)) {
                    continue;
                }
    
                if (!cn.chuanwise.common.util.Arrays.containsIf(convertor.getTargetClasses(), context.getTargetClass()::isAssignableFrom)) {
                    continue;
                }
    
                return convertor.convert(context);
            }
        }
        
        return null;
    }
    
    public static List<String> serialize(SerializeContext context) {
        Preconditions.objectNonNull(context, "context");
    
        try {
            List<String> list = serialize(Priority.HIGHEST, context);
            if (cn.chuanwise.common.util.Collections.nonEmpty(list)) {
                return list;
            }
    
            list = serialize(Priority.HIGH, context);
            if (cn.chuanwise.common.util.Collections.nonEmpty(list)) {
                return list;
            }
    
            list = serialize(Priority.NORMAL, context);
            if (cn.chuanwise.common.util.Collections.nonEmpty(list)) {
                return list;
            }
    
            list = serialize(Priority.LOW, context);
            if (cn.chuanwise.common.util.Collections.nonEmpty(list)) {
                return list;
            }
    
            list = serialize(Priority.LOWEST, context);
            if (cn.chuanwise.common.util.Collections.nonEmpty(list)) {
                return list;
            }
        
        } catch (Exception e) {
            throw new IllegalArgumentException("serialize failed for " + context.getSource(), e);
        }
    
        throw new IllegalArgumentException("no serializer present for " + context.getSource());
    }
    
    private static List<String> serialize(Priority priority, SerializeContext context) throws Exception {
        final List<MethodSerializeHandler> serializers = SERIALIZERS.get(priority);
        final Object source = context.getSource();
    
        if (cn.chuanwise.common.util.Collections.nonEmpty(serializers)) {
            for (MethodSerializeHandler serializer : serializers) {
                if (!serializer.getSourceClass().isInstance(source)) {
                    continue;
                }
    
                return serializer.serialize(context);
            }
        }
        
        return null;
    }
    
    public static Object deserialize(DeserializeContext context) {
        Preconditions.objectNonNull(context, "context");
    
        try {
            Object object = deserialize(Priority.HIGHEST, context);
            if (Objects.nonNull(object)) {
                return object;
            }
            
            object = deserialize(Priority.HIGH, context);
            if (Objects.nonNull(object)) {
                return object;
            }
            
            object = deserialize(Priority.NORMAL, context);
            if (Objects.nonNull(object)) {
                return object;
            }
            
            object = deserialize(Priority.LOW, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        
            object = deserialize(Priority.LOWEST, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("deserialize failed for " + context.getArguments(), e);
        }
    
        throw new IllegalArgumentException("no deserializer present for " + context.getArguments());
    }
    
    private static Object deserialize(Priority priority, DeserializeContext context) throws Exception {
        final List<MethodDeserializerHandler> deserializers = DESERIALIZERS.get(priority);
        final List<String> arguments = context.getArguments();
        
        if (cn.chuanwise.common.util.Collections.nonEmpty(deserializers)) {
            for (MethodDeserializerHandler deserializer : deserializers) {
                
                final List<DeserializerFormatElement> elements = deserializer.getElements();
                
                int index = 0;
                boolean matched = true;
                
                while (index < elements.size() && index < arguments.size()) {
                    final DeserializerFormatElement element = elements.get(index);
                    final String argument = arguments.get(index);
    
                    if (element instanceof DeserializerFormatElement.PlainText) {
                        final DeserializerFormatElement.PlainText plainText = (DeserializerFormatElement.PlainText) element;
                        if (!Objects.equals(argument, plainText.getText())) {
                            matched = false;
                            break;
                        }
                    }
                    
                    index++;
                }
                
                if (!matched) {
                    continue;
                }
                
                if (index < arguments.size() && !elements.isEmpty()) {
                    final DeserializerFormatElement lastElement = elements.get(elements.size() - 1);
    
                    if (!(lastElement instanceof DeserializerFormatElement.RemainParameter)) {
                        continue;
                    }
                }
    
                return deserializer.invoke(context);
            }
        }
        
        return null;
    }
    
    public static String summary(SummaryContext context) {
        Preconditions.objectNonNull(context, "context");
    
        try {
            String object = summary(Priority.HIGHEST, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        
            object = summary(Priority.HIGH, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        
            object = summary(Priority.NORMAL, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        
            object = summary(Priority.LOW, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        
            object = summary(Priority.LOWEST, context);
            if (Objects.nonNull(object)) {
                return object;
            }
        
        } catch (Exception e) {
            throw new IllegalArgumentException("summarize failed for " + context.getSource(), e);
        }
    
        throw new IllegalArgumentException("no summarizer present for " + context.getSource());
    }
    
    private static String summary(Priority priority, SummaryContext context) throws Exception {
        final List<MethodSummaryHandler> summarizers = SUMMARIZERS.get(priority);
        final AutoSummarizable source = context.getSource();
    
        if (cn.chuanwise.common.util.Collections.nonEmpty(summarizers)) {
            for (MethodSummaryHandler summarizer : summarizers) {
                if (!summarizer.getSourceClass().isInstance(source)) {
                    continue;
                }
    
                return summarizer.summary(context);
            }
        }
    
        return null;
    }
    
    public static void registerModule(Object module) {
        Preconditions.objectNonNull(module, "module");
    
        final Class<?> objectClass = module.getClass();
        final Method[] declaredMethods = objectClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            
            // convertor
            final Convertor convertor = method.getAnnotation(Convertor.class);
            if (Objects.nonNull(convertor)) {
                final List<MethodConvertHandler> convertors = Maps.getOrPutGet(CONVERTORS, convertor.priority(), ArrayList::new);
                convertors.add(new MethodConvertHandler(module, method, convertor.value(), convertor.targets()));
            }
    
            // serializer
            final Serializer serializer = method.getAnnotation(Serializer.class);
            if (Objects.nonNull(serializer)) {
                final List<MethodSerializeHandler> serializers = Maps.getOrPutGet(SERIALIZERS, serializer.priority(), ArrayList::new);
                serializers.add(new MethodSerializeHandler(module, method, serializer.value()));
            }
    
            // deserializer
            final Deserializer deserializer = method.getAnnotation(Deserializer.class);
            if (Objects.nonNull(deserializer)) {
                // compile format
                final List<DeserializerFormatElement> elements = compileDeserializerFormat(deserializer.value());
                final List<MethodDeserializerHandler> deserializers = Maps.getOrPutGet(DESERIALIZERS, deserializer.priority(), ArrayList::new);
                
                deserializers.add(new MethodDeserializerHandler(module, method, elements));
            }
    
            // summarizer
            final Summarizer summarizer = method.getAnnotation(Summarizer.class);
            if (Objects.nonNull(summarizer)) {
                final List<MethodSummaryHandler> summarizers = Maps.getOrPutGet(SUMMARIZERS, summarizer.priority(), ArrayList::new);
                summarizers.add(new MethodSummaryHandler(module, method, summarizer.value()));
            }
        }
    }
    
    public static boolean unregisterModule(Class<?> clazz) {
        Preconditions.objectNonNull(clazz, "class");
        
        final boolean unregistered1 = unregister(Priority.HIGHEST, CONVERTORS, clazz::isInstance);
        final boolean unregistered2 = unregister(Priority.HIGHEST, SERIALIZERS, clazz::isInstance);
        final boolean unregistered3 = unregister(Priority.HIGHEST, DESERIALIZERS, clazz::isInstance);
        final boolean unregistered4 = unregister(Priority.HIGHEST, SUMMARIZERS, clazz::isInstance);
        
        final boolean unregistered5 = unregister(Priority.HIGH, CONVERTORS, clazz::isInstance);
        final boolean unregistered6 = unregister(Priority.HIGH, SERIALIZERS, clazz::isInstance);
        final boolean unregistered7 = unregister(Priority.HIGH, DESERIALIZERS, clazz::isInstance);
        final boolean unregistered8 = unregister(Priority.HIGH, SUMMARIZERS, clazz::isInstance);
        
        final boolean unregistered9 = unregister(Priority.NORMAL, CONVERTORS, clazz::isInstance);
        final boolean unregistered10 = unregister(Priority.NORMAL, SERIALIZERS, clazz::isInstance);
        final boolean unregistered11 = unregister(Priority.NORMAL, DESERIALIZERS, clazz::isInstance);
        final boolean unregistered12 = unregister(Priority.NORMAL, SUMMARIZERS, clazz::isInstance);
        
        final boolean unregistered13 = unregister(Priority.LOW, CONVERTORS, clazz::isInstance);
        final boolean unregistered14 = unregister(Priority.LOW, SERIALIZERS, clazz::isInstance);
        final boolean unregistered15 = unregister(Priority.LOW, DESERIALIZERS, clazz::isInstance);
        final boolean unregistered16 = unregister(Priority.LOW, SUMMARIZERS, clazz::isInstance);
        
        final boolean unregistered17 = unregister(Priority.LOWEST, CONVERTORS, clazz::isInstance);
        final boolean unregistered18 = unregister(Priority.LOWEST, SERIALIZERS, clazz::isInstance);
        final boolean unregistered19 = unregister(Priority.LOWEST, DESERIALIZERS, clazz::isInstance);
        final boolean unregistered20 = unregister(Priority.LOWEST, SUMMARIZERS, clazz::isInstance);
        
        return unregistered1 || unregistered2 || unregistered3 || unregistered4
            || unregistered5 || unregistered6 || unregistered7 || unregistered8
            || unregistered9 || unregistered10 || unregistered11 || unregistered12
            || unregistered13 || unregistered14 || unregistered15 || unregistered16
            || unregistered17 || unregistered18 || unregistered19 || unregistered20;
    }
    
    public static boolean unregisterModule(Object module) {
        Preconditions.objectNonNull(module, "module");
    
        final boolean unregidtered1 = unregister(Priority.HIGHEST, CONVERTORS, x -> Objects.equals(x, module));
        final boolean unregidtered2 = unregister(Priority.HIGHEST, SERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered3 = unregister(Priority.HIGHEST, DESERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered4 = unregister(Priority.HIGHEST, SUMMARIZERS, x -> Objects.equals(x, module));
    
        final boolean unregidtered5 = unregister(Priority.HIGH, CONVERTORS, x -> Objects.equals(x, module));
        final boolean unregidtered6 = unregister(Priority.HIGH, SERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered7 = unregister(Priority.HIGH, DESERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered8 = unregister(Priority.HIGH, SUMMARIZERS, x -> Objects.equals(x, module));
    
        final boolean unregidtered9 = unregister(Priority.NORMAL, CONVERTORS, x -> Objects.equals(x, module));
        final boolean unregidtered10 = unregister(Priority.NORMAL, SERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered11 = unregister(Priority.NORMAL, DESERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered12 = unregister(Priority.NORMAL, SUMMARIZERS, x -> Objects.equals(x, module));
    
        final boolean unregidtered13 = unregister(Priority.LOW, CONVERTORS, x -> Objects.equals(x, module));
        final boolean unregidtered14 = unregister(Priority.LOW, SERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered15 = unregister(Priority.LOW, DESERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered16 = unregister(Priority.LOW, SUMMARIZERS, x -> Objects.equals(x, module));
    
        final boolean unregidtered17 = unregister(Priority.LOWEST, CONVERTORS, x -> Objects.equals(x, module));
        final boolean unregidtered18 = unregister(Priority.LOWEST, SERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered19 = unregister(Priority.LOWEST, DESERIALIZERS, x -> Objects.equals(x, module));
        final boolean unregidtered20 = unregister(Priority.LOWEST, SUMMARIZERS, x -> Objects.equals(x, module));
    
        return unregidtered1 || unregidtered2 || unregidtered3 || unregidtered4
            || unregidtered5 || unregidtered6 || unregidtered7 || unregidtered8
            || unregidtered9 || unregidtered10 || unregidtered11 || unregidtered12
            || unregidtered13 || unregidtered14 || unregidtered15 || unregidtered16
            || unregidtered17 || unregidtered18 || unregidtered19 || unregidtered20;
    }
    
    private static <T> boolean unregister(Priority priority, Map<Priority, List<T>> map, Predicate<T> predicate) {
        final List<T> list = map.get(priority);
        boolean removed = false;
        
        if (cn.chuanwise.common.util.Collections.nonEmpty(list)) {
            removed = list.removeIf(predicate);
            if (removed && list.isEmpty()) {
                map.remove(priority);
            }
        }
        return removed;
    }
    
    @SuppressWarnings("all")
    private static List<DeserializerFormatElement> compileDeserializerFormat(String format) {
        Preconditions.objectNonNull(format, "text");
    
        final int length = format.length();
        if (length == 0) {
            return Collections.emptyList();
        }
    
        final StringBuilder stringBuilder = new StringBuilder();
        final List<DeserializerFormatElement> list = new ArrayList<>();
    
        boolean escaped = false;
    
        for (int i = 0; i < length; i++) {
            final char ch = format.charAt(i);
        
            if (escaped) {
                switch (ch) {
                    case 'b':
                        stringBuilder.append("\b");
                        break;
                    case 'f':
                        stringBuilder.append("\f");
                        break;
                    case 'n':
                        stringBuilder.append("\n");
                        break;
                    case 'r':
                        stringBuilder.append("\r");
                        break;
                    case 't':
                        stringBuilder.append("\t");
                        break;
                    case '\\':
                        stringBuilder.append("\\");
                        break;
                    default:
                        stringBuilder.append(ch);
                }
                escaped = false;
                continue;
            }
            if (ch == '\\') {
                escaped = true;
                continue;
            }
        
            if (ch == ':') {
                final String string = stringBuilder.toString();
                stringBuilder.setLength(0);
                
                switch (string) {
                    case "?":
                        list.add(DeserializerFormatElement.Parameter.INSTANCE);
                        break;
                    case "...":
                    case "??":
                    case "???":
                        list.add(DeserializerFormatElement.RemainParameter.INSTANCE);
                        break;
                    default:
                        list.add(new DeserializerFormatElement.PlainText(string));
                }
            } else {
                stringBuilder.append(ch);
            }
        }
    
        if (stringBuilder.length() > 0) {
            final String string = stringBuilder.toString();
            switch (string) {
                case "?":
                    list.add(DeserializerFormatElement.Parameter.INSTANCE);
                    break;
                case "...":
                case "??":
                case "???":
                    list.add(DeserializerFormatElement.RemainParameter.INSTANCE);
                    break;
                default:
                    list.add(new DeserializerFormatElement.PlainText(string));
            }
        }
    
        return Collections.unmodifiableList(list);
    }
}