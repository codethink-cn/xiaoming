package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.object.PropertyHolder;
import com.chuanwise.xiaoming.api.permission.PermissionAccessible;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;

import com.chuanwise.xiaoming.api.util.InteractorUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Chuanwise
 */
public interface XiaomingUser<C extends XiaomingContact<M, ?>, M extends Message, R extends ReceptionTask> extends ModuleObject, PropertyHolder {
    void setReceptionist(Receptionist receptionist);

    /**
     * 以当前用户的身份替换变量
     * @param format 格式字符串，其中使用 {} 引用下文的变量
     * @param arguments 实参。将按顺序用于替换 format 中的 {}
     * @return 替换后的字符串
     */
    default String replaceArguments(String format, Object... arguments) {
        // 消息中所有的变量都会被替换。
        // 最先替换当前变量
        format = ArgumentUtils.replaceArguments(format, arguments);

        // 替换 Language 中的字句
        format = ArgumentUtils.replaceArguments(format, getXiaomingBot().getLanguageManager().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime());

        // 再替换用户属性中的值
        format = ArgumentUtils.replaceArguments(format, getProperties(), getXiaomingBot().getConfiguration().getMaxIterateTime());
        return format;
    }

    default String replaceLanguage(String language, Object... arguments) {
        return replaceArguments(getXiaomingBot().getLanguageManager().getString(language), arguments);
    }

    C getContact();

    /**
     * 给当前使用者发普通消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    void sendMessage(String message, Object... arguments);

    void sendPrivateMessage(String message, Object... arguments);

    default void sendError(String message, Object... arguments) {
        sendMessage("{error} " + message, arguments);
    }

    default void sendWarning(String message, Object... arguments) {
        sendMessage("{warning} " + message, arguments);
    }

    default void sendPrivateError(String message, Object... arguments) {
        sendPrivateMessage("{error} " + message, arguments);
    }

    default void sendPrivateWarning(String message, Object... arguments) {
        sendPrivateMessage("{warning} " + message, arguments);
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

    default M reply(M quote, String message) {
        return getContact().reply(quote, message);
    }

    default M reply(M quote, M message) {
        return getContact().reply(quote, message);
    }

    default M reply(M quote, MessageChain message) {
        return getContact().reply(quote, message);
    }

    default AsyncResult<M> replyLater(long delay, M quote, MessageChain message) {
        return getContact().replyLater(delay, quote, message);
    }

    default AsyncResult<M> replyLater(long delay, M quote, M message) {
        return getContact().replyLater(delay, quote, message);
    }

    default AsyncResult<M> replyLater(long delay, M quote, String message) {
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

    default AsyncResult<M> replyLatestLater(long delay, MessageChain message) {
        return replyLater(delay, getLatestMessage(), message);
    }

    default AsyncResult<M> replyLatestLater(long delay, String message) {
        return replyLater(delay, getLatestMessage(), message);
    }

    default void onNextInput(M message) {
        final List<M> list = getRecentMessages();
        list.add(message);
        synchronized (list) {
            list.notifyAll();
        }
    }

    void onNextInput(MessageChain messages);

    default void onNextInput(String message) {
        onNextInput(MiraiCode.deserializeMiraiCode(message));
    }


    default M nextInput(long timeout, Runnable onTimeout) {
        return InteractorUtils.waitLastElement(getRecentMessages(), timeout, onTimeout);
    }

    default M nextInput(Runnable onTimeout) {
        return nextInput(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime(), onTimeout);
    }

    default M nextInput(long waitTime) {
        return nextInput(waitTime, () -> {
            setProperty("time", TimeUtils.toTimeString(waitTime));
            sendError("{userNextInputTimeOut}");
            throw new InteractorTimeoutException(getInteractor(), this);
        });
    }

    default M nextInput() {
        return nextInput(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime());
    }

    default PrivateMessage nextPrivateInput(long timeout, Runnable onTimeout) {
        return InteractorUtils.waitLastElement(getReceptionist().getPrivateRecentMessages(), timeout, onTimeout);
    }

    default PrivateMessage nextPrivateInput(Runnable onTimeout) {
        return nextPrivateInput(getXiaomingBot().getConfiguration().getMaxUserPrivateInputWaitTime(), onTimeout);
    }

    default PrivateMessage nextPrivateInput(long waitTime) {
        return nextPrivateInput(waitTime, () -> {
            setProperty("time", TimeUtils.toTimeString(waitTime));
            sendError("{userNextPrivateInputTimeOut}");
            throw new InteractorTimeoutException(getInteractor(), this);
        });
    }

    default PrivateMessage nextPrivateInput() {
        return nextPrivateInput(getXiaomingBot().getConfiguration().getMaxUserPrivateInputWaitTime());
    }

    default GroupMessage nextGroupInput(long timeout, String tag, Runnable onTimeout) {
        return InteractorUtils.waitLastElement(getReceptionist().getOrPutGroupRecentMessage(tag), timeout, onTimeout);
    }

    default GroupMessage nextGroupInput(String tag, Runnable onTimeout) {
        return nextGroupInput(getXiaomingBot().getConfiguration().getMaxUserGroupInputWaitTime(), tag, onTimeout);
    }

    default GroupMessage nextGroupInput(long waitTime, String tag) {
        return nextGroupInput(waitTime, tag, () -> {
            setProperty("time", TimeUtils.toTimeString(waitTime));
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

    default Message nextGlobalInput(long waitTime) {
        return nextGlobalInput(waitTime, () -> {
            setProperty("time", TimeUtils.toTimeString(waitTime));
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
     * @return 如果尚未存储相关信息，返回 {@code null}
     */
    default Account getAccount() {
        return getXiaomingBot().getAccountManager().getAccount(getCode());
    }

    /**
     * 获取或新建用户账户
     * @return 用户账户，不一定存在于外存
     */
    default Account getOrPutAccount() {
        return getXiaomingBot().getAccountManager().getOrPutAccount(getCode());
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
    default PrintWriter getPrintWriter() {
        return getPrintWriters().peek();
    }

    /**
     * 获取缓冲区栈
     * @return
     */
    Stack<PrintWriter> getPrintWriters();

    /**
     * 获取当前的缓冲区
     * @return
     */
    default StringWriter getStringWriter() {
        return getStringWriters().peek();
    }

    /**
     * 获取缓冲区栈
     * @return
     */
    Stack<StringWriter> getStringWriters();

    void enableBuffer();

    void disableBuffer();

    default String getBufferAndClose() {
        final String string = getStringWriter().toString();
        disableBuffer();
        return string;
    }

    /**
     * 判断用户是否屏蔽了某插件
     * @param pluginName 插件名
     * @return 用户是否屏蔽。如果尚未存在该用户，则返回该用户是否有权限启动插件
     */
    default boolean isBlockPlugin(String pluginName) {
        final Account account = getAccount();
        if (Objects.nonNull(account)) {
            return account.isBlockPlugin(pluginName);
        } else {
            return !hasPermission("enable." + pluginName);
        }
    }

    default void appendBuffer(String string) {
        final PrintWriter printWriter = getPrintWriter();
        if (getStringWriter().getBuffer().length() != 0) {
            printWriter.println();
        }
        printWriter.print(string);
    }

    R getReceptionTask();
}