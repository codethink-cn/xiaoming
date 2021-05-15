package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.exception.InteactorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceiptCancelledException;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.util.ArgumentUtil;

import com.chuanwise.xiaoming.api.util.TimeUtil;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Chuanwise
 */
public interface XiaomingUser extends XiaomingObject {
    long MAX_WAIT_TIME = TimeUnit.MINUTES.toMillis(1);

    /**
     * 给当前使用者发普通消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    default boolean sendMessage(Object message, Object... arguments) {
        Objects.requireNonNull(message);
        if (inGroup()) {
            return sendGroupAtMessage(message, arguments);
        } else {
            return sendPrivateMessage(message, arguments);
        }
    }

    /**
     * 发送私聊消息。如果是临时会话，就发到临时会话里
     * @param message 消息格式，使用 {} 表示参数
     * @param arguments 消息参数
     * @return 是否发送成功
     */
    default boolean sendPrivateMessage(Object message, Object... arguments) {
        Objects.requireNonNull(message);
        final String translatedMessage = ArgumentUtil.replaceArguments(message.toString(), arguments);
        try {
            if (isUsingBuffer()) {
                appendBuffer(translatedMessage);
            } else if (inPrivate()) {
                getAsPrivate().sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
            } else if (inTemp()) {
                getAsTempMember().sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
            } else {
                getAsGroupMember().sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
            }
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    default boolean sendPrivateMessage(long who, Object message, Object... arguments) {
        Objects.requireNonNull(message);
        final Friend friend = getXiaomingBot().getMiraiBot().getFriend(who);
        try {
            final String translatedMessage = ArgumentUtil.replaceArguments(message.toString(), arguments);
            if (isUsingBuffer()) {
                appendBuffer(translatedMessage);
            } else {
                friend.sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
            }
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * 在群聊发送消息，不带 @
     * @param group 发送的群
     * @param message 消息格式
     * @param arguments 消息参数
     * @return 是否发送成功
     */
    default boolean sendGroupMessage(long group, Object message, Object... arguments) {
        Objects.requireNonNull(message);
        try {
            final Group groupContact = getXiaomingBot().getMiraiBot().getGroup(group);
            if (Objects.nonNull(groupContact)) {
                final String translatedMessage = ArgumentUtil.replaceArguments(message.toString(), arguments);
                if (isUsingBuffer()) {
                    appendBuffer(translatedMessage);
                } else {
                    groupContact.sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    default boolean sendGroupMessage(Object message, Object... arguments) {
        final String translatedMessage = ArgumentUtil.replaceArguments(message.toString(), arguments);
        if (isUsingBuffer()) {
            appendBuffer(translatedMessage);
            return true;
        } else {
            try {
                final Group group = getGroup();
                if (Objects.nonNull(group)) {
                    group.sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
                    return true;
                } else {
                    return false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }
    }

    /**
     * 在群聊发送消息，不带 @
     * @param group 发送的群
     * @param who 要 @ 的人的 QQ
     * @param message 消息格式
     * @param arguments 消息参数
     * @return 是否发送成功
     */
    default boolean sendGroupAtMessage(long group, long who, Object message, Object... arguments) {
        Objects.requireNonNull(message);
        return sendGroupMessage(group, new At(who).serializeToMiraiCode() + " " + message.toString(), arguments);
    }

    /**
     * 在群聊发送消息，不带 @
     * @param message 消息格式
     * @param arguments 消息参数
     * @return 是否发送成功
     */
    default boolean sendGroupAtMessage(Object message, Object... arguments) {
        Objects.requireNonNull(message);
        return sendGroupMessage(new At(getQQ()).serializeToMiraiCode() + " " + message.toString(), arguments);
    }

    /**
     * 给当前使用者发错误消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    default boolean sendError(Object message, Object... arguments) {
        return sendMessage(getXiaomingBot().getWordManager().get("error") + " " + message.toString(), arguments);
    }

    /**
     * 发送私聊错误消息
     * @param message 消息格式，使用 {} 表示参数
     * @param arguments 消息参数
     * @return 是否发送成功
     */
    default boolean sendPrivateError(Object message, Object... arguments) {
        return sendPrivateMessage(getXiaomingBot().getWordManager().get("error") + " " + message.toString(), arguments);
    }

    /**
     * 给当前使用者发警告消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    default boolean sendWarn(Object message, Object... arguments) {
        return sendMessage(getXiaomingBot().getWordManager().get("warning") + " " + message.toString(), arguments);
    }

    /**
     * 发送私聊警告消息
     * @param message 消息格式，使用 {} 表示参数
     * @param arguments 消息参数
     * @return 是否发送成功
     */
    default boolean sendPrivateWarn(Object message, Object... arguments) {
        return sendPrivateMessage(getXiaomingBot().getWordManager().get("warning") + " " + message.toString(), arguments);
    }

    /**
     * 检查用户是否具有某个权限
     * @param node 一些权限节点
     * @return 查询结果
     */
    default boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    default boolean hasPermission(String[] nodes) {
        for (String node : nodes) {
            if (!hasPermission(node)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得该用户的接待员
     * @return 用户接待员
     */
    Receiptionist getReceptionist();

    /**
     * 等待用户的下一次输入，会阻塞本线程
     * @param waitTime 最长等待时间
     * @param onTimeout 超时后的回调函数
     * @return 用户下一次输入的内容，或为 {@code null}
     */
    default String nextInput(long waitTime, Function<Void, Void> onTimeout) {
        setMessage(null);
        long timeOutTime = System.currentTimeMillis() + waitTime;

        // 在用户上等待下一次输入
        try {
            synchronized (this) {
                wait(waitTime);
            }
        } catch (InterruptedException ignored) {
        }
        final String message = getMessage();
        if (Objects.nonNull(message)) {
            return message;
        } else if (System.currentTimeMillis() > timeOutTime) {
            onTimeout.apply(null);
            return null;
        } else {
            throw new ReceiptCancelledException();
        }
    }

    default String nextInput(Function<Void, Void> onTimeout) {
        return nextInput(MAX_WAIT_TIME, onTimeout);
    }

    default String nextInput() {
        return nextInput(MAX_WAIT_TIME, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteactorTimeoutException();
        });
    }

    default String nextInput(long maxWaitTime) {
        return nextInput(maxWaitTime, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteactorTimeoutException();
        });
    }

    /**
     * 获得当前用户的群聊身份
     * @return 返回群聊用户的引用。失败时返回 {@code null}
     */
    Member getAsGroupMember();

    void setAsGroupMember(Member member);

    default boolean inGroup() {
        return Objects.nonNull(getAsGroupMember());
    }

    /**
     * 获得当前用户的临时通话身份
     * @return 返回通话身份的引用。失败时返回 {@code null}
     */
    Member getAsTempMember();

    void setAsTempMember(Member member);

    default boolean inTemp() {
        return Objects.nonNull(getAsTempMember());
    }

    /**
     * 获得当前用户的私聊身份
     * @return 返回私聊会话的引用。失败时返回 {@code null}
     */
    Friend getAsPrivate();

    default boolean inPrivate() {
        return Objects.nonNull(getAsPrivate());
    }

    void setAsPrivate(Friend friend);

    /**
     * 获取用户所在的群
     * @return 如果是群聊用户或临时会话，返回其所在群，否则返回 {@code null}
     */
    default Group getGroup() {
        if (inGroup()) {
            return getAsGroupMember().getGroup();
        } else if (inTemp()) {
            return getAsTempMember().getGroup();
        } else {
            return null;
        }
    }

    /**
     * 获取用户的 QQ
     * @return 用户 QQ
     */
    long getQQ();

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

    /**
     * 获取用户本次的消息的 Mirai 码
     * @return 用户当前消息
     */
    String getMessage();

    /**
     * 设置用户本次的消息
     * @param message
     */
    void setMessage(String message);

    /**
     * 获取用户的账户
     * @return 如果尚未存储相关信息，返回 {@code null}
     */
    default Account getAccount() {
        return getXiaomingBot().getAccountManager().getAccount(getQQ());
    }

    /**
     * 获取或新建用户账户
     * @return 用户账户，不一定存在于外存
     */
    default Account getOrPutAccount() {
        return getXiaomingBot().getAccountManager().getOrPutAccount(getQQ());
    }

    /**
     * 判断是否正在使用缓冲区
     * @return useBuffer
     */
    boolean isUsingBuffer();

    void setUsingBuffer(boolean usingBuffer);

    StringBuilder getBuffer();

    default void clearBuffer() {
        getBuffer().setLength(0);
    }

    default void enableBuffer() {
        setUsingBuffer(true);
        clearBuffer();
    }

    default String getBufferAndClear() {
        final String string = getBuffer().toString();
        clearBuffer();
        setUsingBuffer(false);
        return string;
    }

    /**
     * 获得最近的几次有效交互输入
     * @return 最近的几次有效交互输入
     */
    List<String> getRecentInputs();

    void clearRecentInputs();

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
        final StringBuilder buffer = getBuffer();
        if (buffer.length() == 0) {
            buffer.append(string);
        } else {
            buffer.append("\n").append(string);
        }
    }

    default boolean requirePermission(String node) {
        if (!hasPermission(node)) {
            sendError("小明不能帮你做这件事哦，因为你缺少权限：{}", node);
            return false;
        } else {
            return true;
        }
    }

    default ResponseGroup getResponseGroup() {
        final Group group = getGroup();
        if (Objects.nonNull(group)) {
            return getXiaomingBot().getResponseGroupManager().fromCode(group.getId());
        } else {
            return null;
        }
    }
}
