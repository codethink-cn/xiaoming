package cn.codethink.xiaoming.message.parser;

import cn.chuanwise.common.util.*;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.Objects;

/**
 * 基础消息解析器工具。用于注册基础消息解析器。
 *
 * @author Chuanwise
 */
public class MessageParsers
    extends StaticUtilities {
    
    /**
     * 方法参数填充器
     */
    private interface Wirer {
    
        /**
         * 填充方法参数
         *
         * @param arguments 基础消息参数
         * @param bot       对应的 Bot
         * @return 方法参数值
         */
        Object wire(List<String> arguments, Bot bot);
    }
    
    /**
     * 填充 Bot 的参数填充器
     */
    private static class BotWirer
        implements Wirer {
        
        private static final BotWirer INSTANCE = new BotWirer();
        
        private BotWirer() {
        }
    
        public static BotWirer getInstance() {
            return INSTANCE;
        }
    
        @Override
        public Object wire(List<String> arguments, Bot bot) {
            return bot;
        }
    }
    
    /**
     * 填充具体某个参数的填充器
     */
    @Data
    private static class ArgumentWirer
        implements Wirer {
        
        private final int index;
    
        @Override
        public Object wire(List<String> arguments, Bot bot) {
            return arguments.get(index);
        }
    }
    
    /**
     * 调用方法的解析器
     *
     * @author Chuanwise
     */
    private static class Parser {
    
        /**
         * 解析器方法
         */
        private final Method method;
    
        /**
         * 参数填充器
         */
        private final Wirer[] wirers;
    
        /**
         * 方法调用者
         */
        private final Object source;
    
        public Parser(String[] formats, Method method, Object source) {
            Preconditions.objectNonNull(source, "source");
            Preconditions.objectNonNull(formats, "formats");
        
            final Parameter[] parameters = method.getParameters();
            
            this.source = source;
            this.method = method;
            this.wirers = new Wirer[parameters.length];
            
            final List<Integer> formatArgumentOffsets = new ArrayList<>();
            for (int i = 0; i < formats.length; i++) {
                final String format = formats[i];
                if (Objects.equals(format, "?")) {
                    formatArgumentOffsets.add(i);
                }
            }
            
            int argumentOffsetIndex = 0;
            for (int i = 0; i < parameters.length; i++) {
                final Parameter parameter = parameters[i];
                final Class<?> parameterType = parameter.getType();
    
                if (parameter.isAnnotationPresent(ParserArgument.class)) {
                    wirers[i] = new ArgumentWirer(formatArgumentOffsets.get(argumentOffsetIndex) - 1);
                    argumentOffsetIndex++;
                    continue;
                }
                if (Objects.equals(parameterType, Bot.class)) {
                    wirers[i] = BotWirer.getInstance();
                    continue;
                }
                
                throw new NoSuchElementException();
            }
        }
        
        public Message parse(List<String> arguments, Bot bot) throws Exception {
            Object[] invokeArguments = new Object[wirers.length];
            for (int i = 0; i < wirers.length; i++) {
                invokeArguments[i] = wirers[i].wire(arguments, bot);
            }
    
            try {
                return (Message) Reflections.invokeMethod(source, method, invokeArguments);
            } catch (InvocationTargetException e) {
                Exceptions.rethrow(e.getCause());
                return null;
            }
        }
    }
    
    /**
     * 解析器表
     */
    private static final Map<String, Object> PARSERS = new HashMap<>();
    
    /**
     * 解析若干参数为指定的消息元素
     *
     * @param type      消息元素类型
     * @param arguments 消息元素
     * @param bot       对应的 Bot
     * @return 解析后的消息元素
     * @throws Exception 解析失败时，其中 {@link Throwable#getMessage()} 是失败原因
     */
    @SuppressWarnings("all")
    public static Message parseMessage(String type, List<String> arguments, Bot bot) throws Exception {
        Preconditions.objectNonNull(arguments, "arguments");
//        Preconditions.objectNonNull(bot, "bot");
        // TODO: 2022/4/16
    
        Object object = PARSERS.get(type);
        if (Objects.isNull(object)) {
            throw new NoSuchElementException("unknown basic message type: " + type);
        }
    
        for (int i = 0; i < arguments.size(); i++) {
            final String argument = arguments.get(i);
        
            if (object instanceof Map) {
            
                final Map<String, Object> map = (Map) object;
            
                Object nextObject = map.get(argument);
                if (Objects.nonNull(nextObject)) {
                    object = nextObject;
                    continue;
                }
    
                nextObject = map.get(null);
                if (Objects.nonNull(nextObject)) {
                    object = nextObject;
                    continue;
                }
    
                nextObject = map.get("?");
                if (Objects.nonNull(nextObject)) {
                    object = nextObject;
                    continue;
                }
            
                throw new NoSuchElementException("unknown argument " + (i + 1) + ": " + argument);
            } else {
                throw new NoSuchElementException("unknown format for " + type);
            }
        }
    
        // collect arguments
        if (!(object instanceof Parser)) {
            throw new NoSuchElementException("unknown format for " + type);
        }
        final Parser parser = (Parser) object;
    
        return parser.parse(arguments, bot);
    }
    
    /**
     * 注册一些简单参数解析器
     *
     * @param parsers 简单参数解析器
     */
    @SuppressWarnings("all")
    public static void registerParsers(Object parsers) {
        Preconditions.objectNonNull(parsers, "parsers");
    
        for (Method method : parsers.getClass().getDeclaredMethods()) {
            final MessageParser parser = method.getAnnotation(MessageParser.class);
            if (Objects.isNull(parser)) {
                continue;
            }
        
            Map<String, Object> map = PARSERS;
            final String[] formatWords = parser.value();
            for (int i = 0; i < formatWords.length - 1; i++) {
    
                final Map<String, Object> nextMap;
                if (Objects.equals(formatWords[i], "?")) {
                    nextMap = (Map<String, Object>) Maps.getOrPutGet(map, null, HashMap::new);
                } else {
                    nextMap = (Map<String, Object>) Maps.getOrPutGet(map, formatWords[i], HashMap::new);
                }
                map = nextMap;
            }
        
            map.put(formatWords[formatWords.length - 1], new Parser(formatWords, method, parsers));
        }
    }
}