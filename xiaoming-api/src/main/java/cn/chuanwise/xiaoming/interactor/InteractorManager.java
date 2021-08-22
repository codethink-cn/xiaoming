package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.toolkit.value.container.SimpleValueContainer;
import cn.chuanwise.toolkit.value.container.ValueContainer;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.ReflectUtility;
import cn.chuanwise.utility.ThrowableUtility;
import cn.chuanwise.xiaoming.annotation.Name;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.center.content.ErrorReport;
import cn.chuanwise.xiaoming.center.content.GroupErrorReport;
import cn.chuanwise.xiaoming.client.CenterClientManager;
import cn.chuanwise.xiaoming.interactor.caughter.InteractorThrowableCaughter;
import cn.chuanwise.xiaoming.interactor.caughter.InteractorThrowableCaughterHandler;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.customizer.Customizer;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterContext;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParser;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParserHandler;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.MiraiCodeUtility;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public interface InteractorManager extends ModuleObject {
    /**
     * 和符合条件的指令交互器交互
     * @param user 用户
     * @return 是否交互成功
     * @throws Exception 交互期间抛出的异常
     */
    <M extends Message> boolean interactIf(XiaomingUser<?, M, ?> user, M message, Predicate<InteractorHandler> filter);

    default boolean interactIf(XiaomingUser user, MessageChain messages, Predicate<InteractorHandler> filter) {
        return interactIf(user, user.buildMessage(messages), filter);
    }

    default boolean interactIf(XiaomingUser user, String message, Predicate<InteractorHandler> filter) {
        return interactIf(user, MiraiCode.deserializeMiraiCode(message), filter);
    }

    default boolean interactIf(XiaomingUser user, SingleMessage singleMessage, Predicate<InteractorHandler> filter) {
        return interactIf(user, MiraiCodeUtility.asMessageChain(singleMessage), filter);
    }

    default <M extends Message> boolean interact(XiaomingUser<?, M, ?> user, M message) {
        return interactIf(user, message, null);
    }

    default boolean interact(XiaomingUser user, MessageChain messages) {
        return interactIf(user, messages, null);
    }

    default boolean interact(XiaomingUser user, String message) {
        return interact(user, MiraiCode.deserializeMiraiCode(message));
    }

    default boolean interact(XiaomingUser user, SingleMessage singleMessage) {
        return interact(user, MiraiCodeUtility.asMessageChain(singleMessage));
    }

    /** 交互器 */
    List<InteractorHandler> getInteractors();

    default List<InteractorHandler> getInteractors(Plugin plugin) {
        return CollectionUtility.filter(getInteractors(), new ArrayList<>(), interactor -> (interactor.getPlugin() == plugin));
    }

    void registerInteractor(InteractorHandler interactor);

    default void registerInteractors(Interactors interactors, Customizer customizer, Plugin plugin) {
        if (interactors instanceof PluginObject) {
            final PluginObject pluginObject = (PluginObject) interactors;
            pluginObject.setPlugin(plugin);
            pluginObject.setXiaomingBot(getXiaomingBot());
        }

        interactors.onRegister();
        ReflectUtility.forEachDeclaredMethod(interactors.getClass(), (clazz, method) -> {
            if (method.getAnnotationsByType(Filter.class).length == 0) {
                return;
            }
            InteractorHandler handler = new InteractorHandler(method, plugin);

            // 尝试使用自定义设置
            if (Objects.nonNull(customizer)) {
                final InteractorHandler savedInformation = customizer.forName(handler.getName());
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
        return CollectionUtility.filter(getParameterParsers(), new ArrayList<>(), parser -> (parser.getPlugin() == plugin));
    }

    void unregisterParameterParsers(Plugin plugin);

    /** 用内核 parser 或某插件的 parser 解析 */
    default <T> ValueContainer<T> parseParameter(InteractorParameterContext<T> context) {
        final Plugin plugin = context.getPlugin();
        final Class<T> parameterClass = context.getParameterClass();

        for (InteractorParameterParserHandler handler : getParameterParsers()) {
            if (Objects.nonNull(handler.getPlugin()) && (!handler.isShared() && handler.getPlugin() != plugin)) {
                continue;
            }
            if (!handler.getParameterClass().isAssignableFrom(parameterClass)) {
                continue;
            }

            final ValueContainer<T> result = (ValueContainer<T>) handler.getParser().parse(context);
            if (Objects.isNull(result)) {
                return null;
            } else if (result.hasValue()) {
                return result;
            }
        }
        return null;
    }

    /** 异常捕捉器 */
    List<InteractorThrowableCaughterHandler> getThrowableCaughters();

    <T extends Throwable> void registerThrowableCaughter(InteractorThrowableCaughterHandler<T> handler);

    default <T extends Throwable> void registerThrowableCaughter(Class<T> clazz, InteractorThrowableCaughter<T> caughter, boolean share, Plugin plugin) {
        registerThrowableCaughter(new InteractorThrowableCaughterHandler<>(clazz, caughter, plugin, false));
    }

    void unregisterThrowableCaughters(Plugin plugin);

    default boolean onThrowable(InteractorContext context, Throwable throwable) {
        final Plugin plugin = context.getPlugin();
        final XiaomingUser user = context.getUser();

        for (InteractorThrowableCaughterHandler caughter : getThrowableCaughters()) {
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
        final boolean caughted = Objects.nonNull(throwable);
        if (caughted) {
            user.sendError("{lang.internalError}");
            getLogger().error("和用户 " + user.getCompleteName() + " 交互时出现异常", throwable);
            getXiaomingBot().getReportMessageManager().addThrowableMessage(user, throwable);

            final List<? extends Message> recentMessages = user.getRecentMessages();
            final List<String> serializedMessages = CollectionUtility.addTo(recentMessages, new ArrayList<>(recentMessages.size()), Message::serialize);

            final CenterClientManager clientManager = getXiaomingBot().getCenterClientManager();
            if (clientManager.isConnected()) {
                if (user instanceof GroupXiaomingUser) {
                    clientManager.sendGroupErrorReport(new GroupErrorReport(ErrorReport.XiaomingStatus.valueOf(getXiaomingBot().getStatus().toString()),
                            System.currentTimeMillis(),
                            ((GroupXiaomingUser) user).getGroupCode(),
                            user.getCode(),
                            serializedMessages,
                            ThrowableUtility.writeStackTraces(throwable)
                            ));
                } else {
                    clientManager.sendErrorReport(new ErrorReport(ErrorReport.XiaomingStatus.valueOf(getXiaomingBot().getStatus().toString()),
                            System.currentTimeMillis(),
                            user.getCode(),
                            serializedMessages,
                            ThrowableUtility.writeStackTraces(throwable)
                    ));
                }
            }
        }
        return caughted;
    }

    default void unregisterPlugin(Plugin plugin) {
        unregisterInteractors(plugin);
        unregisterParameterParsers(plugin);
        unregisterThrowableCaughters(plugin);
    }
}