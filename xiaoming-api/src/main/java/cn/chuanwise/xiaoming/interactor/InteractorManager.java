package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.ReflectUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.event.InteractorErrorEvent;
import cn.chuanwise.xiaoming.interactor.caughter.InteractorThrowableCaughter;
import cn.chuanwise.xiaoming.interactor.caughter.InteractorErrorCaughtHandler;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.customizer.Customizer;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterContext;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParser;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParserHandler;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.MiraiCodeUtil;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.*;
import java.util.function.Predicate;

public interface InteractorManager extends ModuleObject {
    /**
     * 和符合条件的指令交互器交互
     * @param user 用户
     * @return 是否交互成功
     * @throws Exception 交互期间抛出的异常
     */
    boolean interactIf(XiaomingUser user, Message message, Predicate<Interactor> filter);

    boolean interactIf(XiaomingUser user, MessageChain messages, Predicate<Interactor> filter);

    default boolean interactIf(XiaomingUser user, String message, Predicate<Interactor> filter) {
        return interactIf(user, MiraiCode.deserializeMiraiCode(message), filter);
    }

    default boolean interactIf(XiaomingUser user, SingleMessage singleMessage, Predicate<Interactor> filter) {
        return interactIf(user, MiraiCodeUtil.asMessageChain(singleMessage), filter);
    }

    default boolean interact(XiaomingUser user, Message message) {
        return interactIf(user, message, null);
    }

    default boolean interact(XiaomingUser user, MessageChain messages) {
        return interactIf(user, messages, null);
    }

    default boolean interact(XiaomingUser user, String message) {
        return interact(user, MiraiCode.deserializeMiraiCode(message));
    }

    default boolean interact(XiaomingUser user, SingleMessage singleMessage) {
        return interact(user, MiraiCodeUtil.asMessageChain(singleMessage));
    }

    /** 交互器 */
    List<Interactor> getInteractors();

    default List<Interactor> getInteractors(Plugin plugin) {
        return CollectionUtil.filter(getInteractors(), new ArrayList<>(), interactor -> (interactor.getPlugin() == plugin));
    }

    void registerInteractor(Interactor interactor);

    default void registerInteractors(Interactors interactors, Customizer customizer, Plugin plugin) {
        if (interactors instanceof PluginObject) {
            final PluginObject pluginObject = (PluginObject) interactors;
            pluginObject.setPlugin(plugin);
            pluginObject.setXiaomingBot(getXiaomingBot());
        }

        interactors.onRegister();
        ReflectUtil.forEachDeclaredMethod(interactors.getClass(), (clazz, method) -> {
            if (method.getAnnotationsByType(Filter.class).length == 0) {
                return;
            }
            Interactor handler = new Interactor(method, plugin);

            // 尝试使用自定义设置
            if (Objects.nonNull(customizer)) {
                final Interactor savedInformation = customizer.forName(handler.getName());
                if (Objects.nonNull(savedInformation)) {
                    handler = savedInformation;
                    handler.setMethod(method);
                    handler.setPlugin(plugin);
                }
            }

            handler.setInteractors(interactors);
            registerInteractor(handler);
        });
    }

    default <T extends Plugin> void registerInteractors(Interactors<T> interactors, T plugin) {
        registerInteractors(interactors, null, plugin);
    }

    void unregisterInteractors(Plugin plugin);

    void unregisterInteractors(Interactors interactors);

    /** 智能参数解析器 */
    List<InteractorParameterParserHandler> getParameterParsers();

    <T> void registerParameterParser(InteractorParameterParserHandler<T> handler);

    default <T> void registerParameterParser(Class<T> clazz, InteractorParameterParser<T> parser, boolean share, Plugin plugin) {
        registerParameterParser(new InteractorParameterParserHandler<>(clazz, parser, plugin, share));
    }

    default List<InteractorParameterParserHandler> getParameterParsers(Plugin plugin) {
        return CollectionUtil.filter(getParameterParsers(), new ArrayList<>(), parser -> (parser.getPlugin() == plugin));
    }

    void unregisterParameterParsers(Plugin plugin);

    /** 用内核 parser 或某插件的 parser 解析 */
    default <T> Container<T> parseParameter(InteractorParameterContext<T> context) {
        final Plugin plugin = context.getPlugin();
        final Class<T> parameterClass = context.getParameterClass();

        for (InteractorParameterParserHandler handler : getParameterParsers()) {
            if (Objects.nonNull(handler.getPlugin()) && (!handler.isShared() && handler.getPlugin() != plugin)) {
                continue;
            }
            if (!handler.getParameterClass().isAssignableFrom(parameterClass)) {
                continue;
            }

            final Container<T> result = (Container<T>) handler.getParser().parse(context);
            if (Objects.isNull(result)) {
                return null;
            } else if (result.hasValue()) {
                return result;
            }
        }
        return null;
    }

    /** 异常捕捉器 */
    List<InteractorErrorCaughtHandler> getThrowableCaughters();

    <T extends Throwable> void registerThrowableCaughter(InteractorErrorCaughtHandler<T> handler);

    default <T extends Throwable> void registerThrowableCaughter(Class<T> clazz, InteractorThrowableCaughter<T> caughter, boolean share, Plugin plugin) {
        registerThrowableCaughter(new InteractorErrorCaughtHandler<>(clazz, caughter, plugin, false));
    }

    void unregisterThrowableCaughters(Plugin plugin);

    default void onThrowable(InteractorContext context, Throwable throwable) {
        final Plugin plugin = context.getPlugin();
        final XiaomingUser user = context.getUser();

        for (InteractorErrorCaughtHandler caughter : getThrowableCaughters()) {
            if (Objects.nonNull(caughter.getPlugin()) && !caughter.isShared() && plugin != caughter.getPlugin()) {
                continue;
            }

            try {
                caughter.getCaughter().caught(context, throwable);
                throwable = null;
                break;
            } catch (Throwable nextThrowable) {
                throwable = nextThrowable;
            }
        }

        if (Objects.nonNull(throwable)) {
            final InteractorErrorEvent event = new InteractorErrorEvent(context, throwable);
            getXiaomingBot().getEventManager().callEventAsync(event);
        }
    }

    default void unregisterPlugin(Plugin plugin) {
        unregisterInteractors(plugin);
        unregisterParameterParsers(plugin);
        unregisterThrowableCaughters(plugin);
    }
}