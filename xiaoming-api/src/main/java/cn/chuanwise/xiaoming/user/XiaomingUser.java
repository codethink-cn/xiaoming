package cn.chuanwise.xiaoming.user;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.attribute.AttributeType;
import cn.chuanwise.xiaoming.client.CenterClient;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.exception.InteractInterrtuptedException;
import cn.chuanwise.xiaoming.exception.InteractExitedException;
import cn.chuanwise.xiaoming.exception.InteractTimeoutException;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.MultipleLanguageFinder;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.message.MessageSendable;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.attribute.AttributeHolder;
import cn.chuanwise.xiaoming.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.tag.PluginBlockable;
import cn.chuanwise.xiaoming.tag.TagHolder;
import cn.chuanwise.xiaoming.recept.ReceptionTask;

import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.PrintWriter;
import java.util.*;

/**
 * @author Chuanwise
 */
public interface XiaomingUser<C extends XiaomingContact<?>>
        extends ModuleObject, AttributeHolder, TagHolder, PluginBlockable, MessageSendable<Optional<Message>> {
    ReceptionTask<XiaomingUser<C>> getReceptionTask();

    void setReceptionTask(ReceptionTask<XiaomingUser<C>> receptionTask);

    void setReceptionist(Receptionist receptionist);

    void setInteractorContext(InteractorContext interactorContext);

    InteractorContext getInteractorContext();

    @Override
    default Set<String> originalTags() {
        return CollectionUtility.asSet(RECORDED, getCodeString());
    }

    CommandRecord buildCommandRecord(String command);

    /**
     * 以当前用户的身份替换变量
     * @param format 格式字符串，其中使用 {} 引用下文的变量
     * @param contexts 实参。将按顺序用于替换 format 中的 {}
     * @return 替换后的字符串
     */
    @Override
    default String format(String format, Object... contexts) {
        final LanguageManager languageManager = getXiaomingBot().getLanguageManager();

        // 替换 Language 中的字句
        return languageManager.formatAdditional(format, variable -> {
            final InteractorContext context = getInteractorContext();
            switch (variable) {
                case "user":
                    return XiaomingUser.this;
                default:
            }
            if (Objects.isNull(context)) {
                return null;
            }
            switch (variable) {
                case "interactor":
                case "command":
                case "interact":
                    return context.getInteractor();
                case "interactors":
                    return context.getInteractor().getInteractors();
                case "lang":
                    return new MultipleLanguageFinder(context.getPlugin(), getXiaomingBot());
                case "contact":
                    return getContact();
                case "plugin":
                    return context.getPlugin();
                case "arguments":
                case "argument":
                case "args":
                case "arg":
                    return context.getArguments();
                default:
                    return null;
            }
        }, contexts);
    }

    @Override
    default String format(Sentence sentence, Object... arguments) {
        final LanguageManager languageManager = getXiaomingBot().getLanguageManager();
        return languageManager.formatAdditional(sentence, variable -> {
            if (Objects.equals(variable, "user")) {
                return this;
            } else {
                return null;
            }
        }, arguments);
    }

    @Override
    default Optional<Message> sendMessage(MessageChain messageChain) {
        if (isUsingBuffer()) {
            appendBuffer(messageChain.serializeToMiraiCode());
            return Optional.empty();
        } else {
            return Optional.of(getContact().sendMessage(messageChain));
        }
    }

    default boolean isBot() {
        return getCode() == getXiaomingBot().getMiraiBot().getId();
    }

    C getContact();

    Message sendPrivateMessage(String message, Object... arguments);

    default Message sendPrivateMessage(MessageChain messages) {
        return sendPrivateMessage(messages.serializeToMiraiCode());
    }

    default Message privateReply(Message quote, String message) {
        return privateReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default Message privateReply(Message quote, Message message) {
        return privateReply(quote, message.getMessageChain());
    }

    default Message privateReply(Message quote, MessageChain message) {
        return sendPrivateMessage(new QuoteReply(quote.getOriginalMessageChain()).plus(message).serializeToMiraiCode());
    }

    default void sendPrivateError(String message, Object... arguments) {
        sendPrivateMessage(getXiaomingBot().getLanguageManager().getSentenceValue("error") + " " + message, arguments);
    }

    default void sendPrivateWarning(String message, Object... arguments) {
        sendPrivateMessage(getXiaomingBot().getLanguageManager().getSentenceValue("warning") + " " + message, arguments);
    }

    default boolean hasPermission(String require) {
        return getXiaomingBot().getPermissionManager().userAccessible(this, require) == PermissionAccessible.ACCESSABLE;
    }

    default boolean hasPermission(String... require) {
        for (String node : require) {
            if (!hasPermission(node)) {
                return false;
            }
        }
        return true;
    }

    default boolean requirePermission(String require) {
        if (hasPermission(require)) {
            return true;
        } else {
            sendError("{lang.lackPermission}", require);
            return false;
        }
    }

    default boolean requirePermission(String... nodes) {
        for (String node : nodes) {
            if (!requirePermission(node)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得该用户的接待员
     * @return 用户接待员
     */
    Receptionist getReceptionist();

    default void onNextInput(Message message) {
        setAttribute(AttributeType.LAST, message);
        getXiaomingBot().getContactManager()
                .onNextMessageEvent(new MessageEvent(this, message));
    }

    void onNextInput(MessageChain messages);

    default void onNextInput(String message) {
        onNextInput(MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().format(message)));
    }

    default Optional<Message> nextInput(long timeout) throws InterruptedException, InteractExitedException {
        final Optional<Message> optional = getContact().nextMessage(timeout);
        if (optional.isPresent()) {
            final Message message = optional.get();
            final String serializedMessage = message.serialize();

            if (Objects.equals(serializedMessage, "退出")) {
                throw new InteractExitedException();
            } else {
                final CommandRecord commandRecord = buildCommandRecord(serializedMessage);
                final Account account = getAccount();
                account.addCommand(commandRecord);
                getXiaomingBot().getFileSaver().readyToSave(account);

                final CenterClient client = getXiaomingBot().getCenterClient();
                client.doOrFail(client::increaseTotalCallNumber, "增加总小明调用次数");

                return optional;
            }
        } else {
            return optional;
        }
    }

    default Optional<Message> nextInput() throws InterruptedException, InteractExitedException {
        return nextInput(getXiaomingBot().getConfiguration().getMaxUserInputTimeout());
    }

    default Message nextMessageOrExit(long timeout) throws InteractExitedException {
        try {
            return nextInput(timeout)
                    .orElseThrow(() -> new InteractTimeoutException(getInteractorContext(), this, timeout));
        } catch (InterruptedException exception) {
            throw new InteractInterrtuptedException(getInteractorContext(), this);
        }
    }

    default Message nextMessageOrExit() throws InteractExitedException {
        return nextMessageOrExit(getXiaomingBot().getConfiguration().getMaxUserInputTimeout());
    }

    default Optional<Message> nextPrivateInput(long timeout) throws InterruptedException {
        return getXiaomingBot()
                .getContactManager()
                .nextPrivateMessage(getCode(), timeout)
                .map(MessageEvent::getMessage);
    }

    default Optional<Message> nextPrivateInput() throws InterruptedException {
        return nextPrivateInput(getXiaomingBot().getConfiguration().getMaxUserPrivateInputTimeout());
    }

    default Optional<Message> nextGroupInput(String tag, long timeout) throws InterruptedException {
        return getXiaomingBot().getContactManager()
                .nextGroupMemberMessage(tag, getCode(), timeout)
                .map(MessageEvent::getMessage);
    }

    default Optional<Message> nextGroupInput(String tag) throws InterruptedException {
        return nextGroupInput(tag, getXiaomingBot().getConfiguration().getMaxUserGroupInputTimeout());
    }

    default Optional<Message> nextGlobalInput(long timeout) throws InterruptedException {
        return getXiaomingBot()
                .getContactManager()
                .nextMessageEvent(timeout, messageEvent -> messageEvent.getUser().getCode() == getCode())
                .map(MessageEvent::getMessage);
    }

    default Optional<Message> nextGlobalInput() throws InterruptedException {
        return nextGlobalInput(getXiaomingBot().getConfiguration().getMaxUserInputTimeout());
    }

    /**
     * 获取用户的 QQ
     * @return 用户 QQ
     */
    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    /**
     * 获取用户的 QQ 账号名或备注
     * @return QQ 账号名或备注
     */
    default String getName() {
        return getContact().getName();
    }

    /**
     * 获取用户全名。包含所在的群的名称。
     * @return 用户全名
     */
    String getCompleteName();

    /**
     * 获取用户的账户
     * @return 如果尚未存储相关信息，则创建，但不一定会立刻写入外存。
     */
    default Account getAccount() {
        final Account account = getXiaomingBot().getAccountManager().getAccount(getCode());
        if (Objects.isNull(account)) {
            account.setAlias(getAlias());
        }
        return account;
    }

    /**
     * 判断是否正在使用缓冲区
     * @return useBuffer
     */
    boolean isUsingBuffer();

    /**
     * 获取当前的缓冲区
     * @return
     */
    PrintWriter getPrintWriter();

    void enablePrintWriter();

    void disablePrintWriter();

    /**
     * 取出缓存区中的内容，并
     * @return
     */
    String getBufferAndClose();

    default String getAlias() {
        final Account account = getAccount();
        if (StringUtility.isEmpty(account.getAlias())) {
            return getName();
        } else {
            return account.getAlias();
        }
    }

    @Override
    default Set<String> getTags() {
        return getXiaomingBot().getAccountManager().getTags(getCode());
    }

    default String getAliasAndCode() {
        return getAlias() + "（" + getCodeString() + "）";
    }

    default String getAliasOrCode() {
        final Account account = getAccount();
        if (StringUtility.isEmpty(account.getAlias())) {
            return getCodeString();
        } else {
            return account.getAlias();
        }
    }

    void appendBuffer(String string);

    void nudge();

    default Image uploadImage(ExternalResource resource) {
        return getContact().uploadImage(resource);
    }
}