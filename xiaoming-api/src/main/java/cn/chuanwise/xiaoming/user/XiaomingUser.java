package cn.chuanwise.xiaoming.user;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.exception.InteractorTimeoutException;
import cn.chuanwise.xiaoming.exception.ReceptCancelledException;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.object.PropertyHolder;
import cn.chuanwise.xiaoming.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.recept.ReceptionTask;

import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Chuanwise
 */
public interface XiaomingUser<C extends XiaomingContact<M, ?>, M extends Message, R extends ReceptionTask> extends ModuleObject, PropertyHolder {
    void setReceptionist(Receptionist receptionist);

    void setInteractor(Interactor interactor);

    /**
     * 以当前用户的身份替换变量
     * @param format 格式字符串，其中使用 {} 引用下文的变量
     * @param arguments 实参。将按顺序用于替换 format 中的 {}
     * @return 替换后的字符串
     */
    default String replaceArguments(String format, Object... arguments) {
        // 消息中所有的变量都会被替换。
        // 最先替换当前变量
        format = ArgumentUtility.replaceArguments(format, arguments);

        // 如果正在用插件，且插件的语言非空，也替换插件的语言
        if (Objects.nonNull(getInteractor()) &&
                Objects.nonNull(getInteractor().getPlugin()) &&
                Objects.nonNull(getInteractor().getPlugin().getLanguage()) &&
                !CollectionUtility.isEmpty(getInteractor().getPlugin().getLanguage().getValues().entrySet())) {
            format = ArgumentUtility.replaceArguments(format, getInteractor().getPlugin().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime());
        }

        // 替换 Language 中的字句
        format = ArgumentUtility.replaceArguments(format, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime());

        // 再替换用户属性中的值
        format = ArgumentUtility.replaceArguments(format, getProperties(), getXiaomingBot().getConfiguration().getMaxIterateTime());
        return format;
    }

    default String replaceLanguage(String language, Object... arguments) {
        return replaceArguments(getXiaomingBot().getLanguage().getString(language), arguments);
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

    default void sendMessage(MessageChain messages) {
        sendMessage(messages.serializeToMiraiCode());
    }

    default void sendError(MessageChain messages) {
        sendMessage(messages.serializeToMiraiCode());
    }

    default void sendWarning(MessageChain messages) {
        sendMessage(messages.serializeToMiraiCode());
    }

    default ScheduledFuture<Boolean> sendMessageLater(long delay, String message, Object... arguments) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> {
            sendMessage(message, arguments);
            return true;
        });
    }

    Message sendPrivateMessage(String message, Object... arguments);

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
        sendMessage(getXiaomingBot().getLanguage().getString("error") + " " + message, arguments);
    }

    default void sendWarning(String message, Object... arguments) {
        sendMessage(getXiaomingBot().getLanguage().getString("warning") + " " + message, arguments);
    }

    default void sendPrivateError(String message, Object... arguments) {
        sendPrivateMessage(getXiaomingBot().getLanguage().getString("error") + " " + message, arguments);
    }

    default void sendPrivateWarning(String message, Object... arguments) {
        sendPrivateMessage(getXiaomingBot().getLanguage().getString("warning") + " " + message, arguments);
    }

    default void sendErrorLater(long delay, String message, Object... arguments) {
        sendMessageLater(delay, getXiaomingBot().getLanguage().getString("error") + " " + message, arguments);
    }

    default void sendWarningLater(long delay, String message, Object... arguments) {
        sendMessageLater(delay, getXiaomingBot().getLanguage().getString("warning") + " " + message, arguments);
    }

    default void sendPrivateErrorLater(long delay, String message, Object... arguments) {
        sendPrivateMessageLater(delay, getXiaomingBot().getLanguage().getString("error") + " " + message, arguments);
    }

    default void sendPrivateWarningLater(long delay, String message, Object... arguments) {
        sendPrivateMessageLater(delay, getXiaomingBot().getLanguage().getString("warning") + " " + message, arguments);
    }

    default boolean hasPermission(String require) {
        setProperty("permission", require);
        return getXiaomingBot().getPermissionManager().userAccessible(this, require) == PermissionAccessible.ACCESSABLE;
    }

    default boolean hasPermission(String[] require) {
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
            sendError("{lackPermission}");
            return false;
        }
    }

    default boolean requirePermission(String[] nodes) {
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

    Interactor getInteractor();

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

    void onNextInput(MessageChain messages);

    default void onNextInput(String message) {
        onNextInput(MiraiCode.deserializeMiraiCode(ArgumentUtility.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default M nextInput(long timeout, Runnable onTimeout) {
        final M lastElement = InteractorUtility.waitLastElement(getRecentMessages(), timeout, onTimeout);
        if (Objects.equals(lastElement.serialize(), "退出")) {
            sendMessage("退出成功");
            throw new ReceptCancelledException();
        } else {
            return lastElement;
        }
    }

    default M nextInput(Runnable onTimeout) {
        return nextInput(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime(), onTimeout);
    }

    default M nextInput(long timeout) {
        return nextInput(timeout, () -> {
            setProperty("time", TimeUtility.toTimeLength(timeout));
            sendError("{userNextInputTimeOut}");
            throw new InteractorTimeoutException(getInteractor(), this);
        });
    }

    default M nextInput() {
        return nextInput(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime());
    }

    default PrivateMessage nextPrivateInput(long timeout, Runnable onTimeout) {
        return InteractorUtility.waitLastElement(getReceptionist().forPrivateRecentMessages(), timeout, onTimeout);
    }

    default PrivateMessage nextPrivateInput(Runnable onTimeout) {
        return nextPrivateInput(getXiaomingBot().getConfiguration().getMaxUserPrivateInputWaitTime(), onTimeout);
    }

    default PrivateMessage nextPrivateInput(long timeout) {
        return nextPrivateInput(timeout, () -> {
            setProperty("time", TimeUtility.toTimeLength(timeout));
            sendError("{userNextPrivateInputTimeOut}");
            throw new InteractorTimeoutException(getInteractor(), this);
        });
    }

    default PrivateMessage nextPrivateInput() {
        return nextPrivateInput(getXiaomingBot().getConfiguration().getMaxUserPrivateInputWaitTime());
    }

    default GroupMessage nextGroupInput(long timeout, String tag, Runnable onTimeout) {
        return InteractorUtility.waitLastElement(getReceptionist().forGroupRecentMessages(tag), timeout, onTimeout);
    }

    default GroupMessage nextGroupInput(String tag, Runnable onTimeout) {
        return nextGroupInput(getXiaomingBot().getConfiguration().getMaxUserGroupInputWaitTime(), tag, onTimeout);
    }

    default GroupMessage nextGroupInput(long timeout, String tag) {
        return nextGroupInput(timeout, tag, () -> {
            setProperty("time", TimeUtility.toTimeLength(timeout));
            setProperty("tag", tag);
            sendError("{userNextGroupInputTimeOut}");
            throw new InteractorTimeoutException(getInteractor(), this);
        });
    }

    default GroupMessage nextGroupInput(String tag) {
        return nextGroupInput(getXiaomingBot().getConfiguration().getMaxUserGroupInputWaitTime(), tag);
    }

    default Message nextGlobalInput(long timeout, Runnable onTimeout) {
        // 在用户上等待下一次输入
        final long latestTime = System.currentTimeMillis() + timeout;
        final Receptionist receptionist = getReceptionist();

        try {
            synchronized (receptionist) {
                receptionist.wait(timeout);
            }
        } catch (InterruptedException ignored) {
        }
        if (System.currentTimeMillis() < latestTime) {
            final List<? extends Message> list = receptionist.getGlobalRecentMessages();
            if (Objects.nonNull(list)) {
                return list.get(list.size() - 1);
            } else {
                throw new ReceptCancelledException();
            }
        } else {
            onTimeout.run();
            return null;
        }
    }

    default Message nextGlobalInput(Runnable onTimeout) {
        return nextGlobalInput(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime(), onTimeout);
    }

    default Message nextGlobalInput(long timeout) {
        return nextGlobalInput(timeout, () -> {
            setProperty("time", TimeUtility.toTimeLength(timeout));
            sendError("{userGlobalNextInputTimeOut}");
            throw new InteractorTimeoutException(getInteractor(), this);
        });
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
        return getXiaomingBot().getAccountManager().forAccount(getCode());
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
        if (Objects.nonNull(account) && Objects.nonNull(account.getAlias())) {
            return account.getAlias();
        } else {
            return getName();
        }
    }

    default Set<String> getTags() {
        return getXiaomingBot().getAccountManager().getTags(getCode());
    }

    default boolean hasTag(String tag) {
        return getXiaomingBot().getAccountManager().hasTag(getCode(), tag);
    }

    default void addTag(String tag) {
        getAccount().addTag(tag);
    }

    default void removeTag(String tag) {
        getAccount().removeTag(tag);
    }

    default String getAliasAndCode() {
        return getAlias() + "（" + getCodeString() + "）";
    }

    void appendBuffer(String string);

    R getReceptionTask();

    void nudge();

    default Image uploadImage(ExternalResource resource) {
        return getContact().uploadImage(resource);
    }
}