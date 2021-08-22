package cn.chuanwise.xiaoming.user;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.ObjectUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.account.record.GroupCommandRecord;
import cn.chuanwise.xiaoming.account.record.PrivateCommandRecord;
import cn.chuanwise.xiaoming.client.CenterClientManager;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.exception.InteractorTimeoutException;
import cn.chuanwise.xiaoming.exception.ReceptCancelledException;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.MultipleLanguageFinder;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.attribute.AttributeHolder;
import cn.chuanwise.xiaoming.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.tag.PluginBlockable;
import cn.chuanwise.xiaoming.tag.TagHolder;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.xiaoming.recept.ReceptionTask;

import cn.chuanwise.xiaoming.utility.MiraiCodeUtility;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Chuanwise
 */
public interface XiaomingUser<C extends XiaomingContact<M, ?>, M extends Message, R extends ReceptionTask>
        extends ModuleObject, AttributeHolder, TagHolder, PluginBlockable {
    void setReceptionist(Receptionist receptionist);

    void setInteractorContext(InteractorContext interactorContext);

    InteractorContext getInteractorContext();

    @Override
    default Set<String> buildOriginalTags() {
        return CollectionUtility.asSet(RECORDED, getCodeString());
    }

    /** 以该成员的身份构造一个消息 */
    M buildMessage(MessageChain messages);

    default M buildMessage(String message) {
        return buildMessage(MiraiCode.deserializeMiraiCode(message));
    }

    default M buildMessage(SingleMessage singleMessage) {
        return buildMessage(MiraiCodeUtility.asMessageChain(singleMessage));
    }

    CommandRecord buildCommandRecord(String command);

    /**
     * 以当前用户的身份替换变量
     * @param format 格式字符串，其中使用 {} 引用下文的变量
     * @param contexts 实参。将按顺序用于替换 format 中的 {}
     * @return 替换后的字符串
     */
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
                    return context.getMessage().getContact();
                case "plugin":
                    return context.getPlugin();
                case "arguments":
                case "args":
                case "arg":
                    return context.getArguments();
                default:
                    return null;
            }
        }, contexts);
    }

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

    default boolean isBot() {
        return getCode() == getXiaomingBot().getMiraiBot().getId();
    }

    C getContact();

    /**
     * 给当前使用者发普通消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    void sendMessage(String message, Object... arguments);

    default void sendMessage(Sentence sentence, Object... arguments) {
        sendMessage(format(sentence, arguments));
    }

    default void sendMessage(MessageChain messages) {
        sendMessage(messages.serializeToMiraiCode());
    }

    default void sendError(MessageChain messages) {
        sendMessage(messages.serializeToMiraiCode());
    }

    default void sendError(Sentence sentence, Object... arguments) {
        sendError(format(sentence, arguments));
    }

    default void sendWarning(MessageChain messages) {
        sendMessage(messages.serializeToMiraiCode());
    }

    default void sendWarning(Sentence sentence, Object... arguments) {
        sendWarning(format(sentence, arguments));
    }

    default ScheduledFuture<Boolean> sendMessageLater(long delay, String message, Object... arguments) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> {
            sendMessage(message, arguments);
            return true;
        });
    }

    Message sendPrivateMessage(String message, Object... arguments);

    default Message sendPrivateMessage(MessageChain messages) {
        return sendPrivateMessage(messages.serializeToMiraiCode());
    }

    default Message privateReply(Message quote, String message) {
        return privateReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default Message privateReply(Message quote, GroupMessage message) {
        return privateReply(quote, message.getMessageChain());
    }

    default Message privateReply(Message quote, MessageChain message) {
        return sendPrivateMessage(new QuoteReply(quote.getOriginalMessageChain()).plus(message).serializeToMiraiCode());
    }

    default ScheduledFuture<Message> privateReplyLater(long delay, Message quote, MessageChain message) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> privateReply(quote, message));
    }

    default ScheduledFuture<Message> privateReplyLater(long delay, Message quote, GroupMessage message) {
        return privateReplyLater(delay, quote, message.getMessageChain());
    }

    default ScheduledFuture<Message> privateReplyLater(long delay, Message quote, String message) {
        return privateReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(message));
    }

    default Message privateReplyLaterLatest(String message) {
        return privateReply(getLatestMessage(), MiraiCode.deserializeMiraiCode(message));
    }

    default Message privateReplyLaterLatest(GroupMessage message) {
        return privateReply(getLatestMessage(), message.getMessageChain());
    }

    default Message privateReplyLaterLatest(MessageChain message) {
        return privateReply(getLatestMessage(), message);
    }

    default ScheduledFuture<Message> privateReplyLatestLater(long delay, MessageChain message) {
        return privateReplyLater(delay, getLatestMessage(), message);
    }

    default ScheduledFuture<Message> privateReplyLatestLater(long delay, GroupMessage message) {
        return privateReplyLater(delay, getLatestMessage(), message.getMessageChain());
    }

    default ScheduledFuture<Message> privateReplyLatestLater(long delay, String message) {
        return privateReplyLater(delay, getLatestMessage(), MiraiCode.deserializeMiraiCode(message));
    }

    default ScheduledFuture<Message> sendPrivateMessageLater(long delay, String message, Object... arguments) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> sendPrivateMessage(message, arguments));
    }

    default void sendError(String message, Object... arguments) {
        sendMessage(getXiaomingBot().getLanguageManager().getSentenceValue("error") + " " + message, arguments);
    }

    default void sendWarning(String message, Object... arguments) {
        sendMessage(getXiaomingBot().getLanguageManager().getSentenceValue("warning") + " " + message, arguments);
    }

    default void sendPrivateError(String message, Object... arguments) {
        sendPrivateMessage(getXiaomingBot().getLanguageManager().getSentenceValue("error") + " " + message, arguments);
    }

    default void sendPrivateWarning(String message, Object... arguments) {
        sendPrivateMessage(getXiaomingBot().getLanguageManager().getSentenceValue("warning") + " " + message, arguments);
    }

    default void sendErrorLater(long delay, String message, Object... arguments) {
        sendMessageLater(delay, getXiaomingBot().getLanguageManager().getSentenceValue("error") + " " + message, arguments);
    }

    default void sendWarningLater(long delay, String message, Object... arguments) {
        sendMessageLater(delay, getXiaomingBot().getLanguageManager().getSentenceValue("warning") + " " + message, arguments);
    }

    default void sendPrivateErrorLater(long delay, String message, Object... arguments) {
        sendPrivateMessageLater(delay, getXiaomingBot().getLanguageManager().getSentenceValue("error") + " " + message, arguments);
    }

    default void sendPrivateWarningLater(long delay, String message, Object... arguments) {
        sendPrivateMessageLater(delay, getXiaomingBot().getLanguageManager().getSentenceValue("warning") + " " + message, arguments);
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

    default At getAt() {
        return getReceptionist().getAt();
    }

    default M reply(Message quote, String message) {
        return getContact().reply(quote, message);
    }

    default M reply(Message quote, M message) {
        return getContact().reply(quote, message);
    }

    default M reply(Message quote, MessageChain message) {
        return getContact().reply(quote, message);
    }

    default ScheduledFuture<M> replyLater(long delay, Message quote, MessageChain message) {
        return getContact().replyLater(delay, quote, message);
    }

    default ScheduledFuture<M> replyLater(long delay, Message quote, M message) {
        return getContact().replyLater(delay, quote, message);
    }

    default ScheduledFuture<M> replyLater(long delay, Message quote, String message) {
        return getContact().replyLater(delay, quote, message);
    }

    default M replyLatest(String message) {
        return reply(getLatestMessage(), message);
    }

    default M replyLatest(M message) {
        return reply(getLatestMessage(), message);
    }

    default M replyLatest(MessageChain message) {
        return reply(getLatestMessage(), message);
    }

    default ScheduledFuture<M> replyLatestLater(long delay, MessageChain message) {
        return replyLater(delay, getLatestMessage(), message);
    }

    default ScheduledFuture<M> replyLatestLater(long delay, String message) {
        return replyLater(delay, getLatestMessage(), message);
    }

    void onNextInput(M message);

    default void onNextInput(MessageChain messages) {
        onNextInput(buildMessage(messages));
    }

    default void onNextInput(String message) {
        onNextInput(MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().format(message)));
    }

    default M nextInput(long timeout) {
        try {
            final M receive = InteractorUtility.waitLastElement(getRecentMessages(), timeout);
            if (Objects.equals(receive.serialize(), "退出")) {
                sendMessage("退出成功");
                throw new ReceptCancelledException();
            } else {
                final CommandRecord commandRecord = buildCommandRecord(receive.serialize());
                final Account account = getAccount();
                account.addCommand(commandRecord);
                getXiaomingBot().getFileSaver().readyToSave(account);

                final CenterClientManager client = getXiaomingBot().getCenterClientManager();
                client.doOrFail(client::increaseTotalCallNumber, "增加总小明调用次数");

                return receive;
            }
        } catch (InteractorTimeoutException exception) {
            sendError("{lang.userNextInputTimeout}", timeout);
            throw exception;
        }
    }

    default M nextInput() {
        return nextInput(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime());
    }

    default PrivateMessage nextPrivateInput(long timeout) {
        try {
            final PrivateMessage receive = InteractorUtility.waitLastElement(getReceptionist().forPrivateRecentMessages(), timeout);
            if (Objects.equals(receive.serialize(), "退出")) {
                sendMessage("退出成功");
                throw new ReceptCancelledException();
            } else {
                final CommandRecord commandRecord = new PrivateCommandRecord(receive.serialize());
                final Account account = getAccount();
                account.addCommand(commandRecord);
                getXiaomingBot().getFileSaver().readyToSave(account);

                final CenterClientManager client = getXiaomingBot().getCenterClientManager();
                client.doOrFail(client::increaseTotalCallNumber, "增加总小明调用次数");

                return receive;
            }
        } catch (InteractorTimeoutException exception) {
            sendError("{lang.userNextInputTimeout}", timeout);
            throw exception;
        }
    }

    default PrivateMessage nextPrivateInput() {
        return nextPrivateInput(getXiaomingBot().getConfiguration().getMaxUserPrivateInputWaitTime());
    }

    default GroupMessage nextGroupInput(String tag, long timeout) {
        try {
            final GroupMessage receive = InteractorUtility.waitLastElement(getReceptionist().forGroupRecentMessages(tag), timeout);
            if (Objects.equals(receive.serialize(), "退出")) {
                sendMessage("退出成功");
                throw new ReceptCancelledException();
            } else {
                final CommandRecord commandRecord = new GroupCommandRecord(receive.getContact().getCode(), receive.serialize());
                final Account account = getAccount();
                account.addCommand(commandRecord);
                getXiaomingBot().getFileSaver().readyToSave(account);

                final CenterClientManager client = getXiaomingBot().getCenterClientManager();
                client.doOrFail(client::increaseTotalCallNumber, "增加总小明调用次数");

                return receive;
            }
        } catch (InteractorTimeoutException exception) {
            sendError("{lang.userNextInputTimeout}", timeout);
            throw exception;
        }
    }

    default GroupMessage nextGroupInput(String tag) {
        return nextGroupInput(tag, getXiaomingBot().getConfiguration().getMaxUserGroupInputWaitTime());
    }

    default Message nextGlobalInput(long timeout) {
        final Receptionist receptionist = getReceptionist();

        switch (ObjectUtility.wait(receptionist, timeout)) {
            case NOTIFY:
                return receptionist.getGlobalRecentMessages().get(receptionist.getGlobalRecentMessages().size() - 1);
            case TIMEOUT:
                sendError("{lang.userNextInputTimeout}", timeout);
                throw new InteractorTimeoutException(getInteractorContext(), this);
            case INTERRUPT:
                throw new ReceptCancelledException();
            default:
                throw new UnsupportedVersionException();
        }
    }

    default Message nextGlobalInput() {
        return nextGlobalInput(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime());
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
    String getName();

    /**
     * 获取用户全名。包含所在的群的名称。
     * @return 用户全名
     */
    String getCompleteName();

    List<M> getRecentMessages();

    default M getLatestMessage() {
        final List<M> recentMessages = getRecentMessages();
        if (recentMessages.isEmpty()) {
            return null;
        } else {
            return recentMessages.get(recentMessages.size() - 1);
        }
    }

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

    R getReceptionTask();

    void nudge();

    default Image uploadImage(ExternalResource resource) {
        return getContact().uploadImage(resource);
    }
}