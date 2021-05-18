package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.object.PropertyHolder;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.util.ArgumentUtil;

import com.chuanwise.xiaoming.api.util.TimeUtil;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Chuanwise
 */
public interface XiaomingUser extends HostObject, PropertyHolder {
    long MAX_WAIT_TIME = TimeUnit.MINUTES.toMillis(1);

    void setReceptionist(Receptionist receptionist);

    /**
     * 给当前使用者发普通消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    default boolean sendMessage(String message, Object... arguments) {
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
    default boolean sendPrivateMessage(String message, Object... arguments) {
        Objects.requireNonNull(message);
        final String translatedMessage = ArgumentUtil.replaceArguments(message, arguments);
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

    default boolean sendPrivateMessage(long who, String message, Object... arguments) {
        Objects.requireNonNull(message);
        final Friend friend = getXiaomingBot().getMiraiBot().getFriend(who);
        final String translatedMessage = ArgumentUtil.replaceArguments(message, arguments);
        try {
            if (Objects.nonNull(friend)) {
                friend.sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    default boolean sendPrivateMessage(long group, long who, String message, Object... arguments) {
        Objects.requireNonNull(message);
        final Group miraiGroup = getXiaomingBot().getMiraiBot().getGroup(group);
        if (Objects.nonNull(miraiGroup)) {
            final NormalMember member = miraiGroup.get(who);
            if (Objects.nonNull(member)) {
                member.sendMessage(ArgumentUtil.replaceArguments(message, arguments));
                return true;
            }
        }
        return false;
    }

    /**
     * 在群聊发送消息
     * @param group 发送的群
     * @param message 消息格式
     * @param arguments 消息参数
     * @return 是否发送成功
     */
    default boolean sendGroupMessage(long group, String message, Object... arguments) {
        Objects.requireNonNull(message);
        try {
            final Group groupContact = getXiaomingBot().getMiraiBot().getGroup(group);
            if (Objects.nonNull(groupContact)) {
                final String translatedMessage = ArgumentUtil.replaceArguments(message, arguments);
                groupContact.sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    default boolean sendGroupMessage(String message, Object... arguments) {
        try {
            final String translatedMessage = ArgumentUtil.replaceArguments(message, arguments);
            if (isUsingBuffer()) {
                appendBuffer(translatedMessage);
                return true;
            } else {
                final Group group = getGroup();
                if (Objects.nonNull(group)) {
                    group.sendMessage(MiraiCode.deserializeMiraiCode(translatedMessage));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    default boolean sendGroupAtMessage(long group, long who, String message, Object... arguments) {
        Objects.requireNonNull(message);
        return sendGroupMessage(group, new At(who).serializeToMiraiCode() + " " + message, arguments);
    }

    default boolean sendGroupAtMessage(String message, Object... arguments) {
        Objects.requireNonNull(message);
        if (isUsingBuffer()) {
            appendBuffer(ArgumentUtil.replaceArguments(message, arguments));
            return true;
        } else {
            return sendGroupMessage(new At(getQQ()).serializeToMiraiCode() + " " + message, arguments);
        }
    }

    /**
     * 给当前使用者发错误消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    default boolean sendError(String message, Object... arguments) {
        return sendMessage(getXiaomingBot().getWordManager().get("error") + " " + message, arguments);
    }

    default boolean sendPrivateError(String message, Object... arguments) {
        return sendPrivateMessage(getXiaomingBot().getWordManager().get("error") + " " + message, arguments);
    }

    default boolean sendWarn(String message, Object... arguments) {
        return sendMessage(getXiaomingBot().getWordManager().get("warning") + " " + message, arguments);
    }

    default boolean sendPrivateWarn(String message, Object... arguments) {
        return sendPrivateMessage(getXiaomingBot().getWordManager().get("warning") + " " + message, arguments);
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

    default boolean requirePermission(String node) {
        if (hasPermission(node)) {
            return true;
        } else {
            sendError("小明不能帮你做这件事哦，因为你缺少权限：{}", node);
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
     * 设置用户在当前语境下的下一条消息
     * @param message 消息
     */
    default void setMessage(String message) {
        getReceptionTask().onMessage(message);
    }

    /**
     * 获得该用户的接待员
     * @return 用户接待员
     */
    Receptionist getReceptionist();

    /**
     * 获得用户的下一次输入
     * @param waitTime 最长等待时间。如果为 0 则为无限制等待，不推荐这么使用。
     * @param onTimeout 超时后的操作
     * @return 用户的下一次输入。如果超时返回 {@code null}
     */
    default String nextInput(long waitTime, Function<Void, Void> onTimeout) {
        // 在用户上等待下一次输入
        return getReceptionTask().nextInput(waitTime, onTimeout);
    }

    default String nextInput(Function<Void, Void> onTimeout) {
        return nextInput(MAX_WAIT_TIME, onTimeout);
    }

    default String nextInput() {
        return nextInput(MAX_WAIT_TIME, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextInput(long maxWaitTime) {
        return nextInput(maxWaitTime, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextPrivateInput(long waitTime, Function<Void, Void> onTimeout) {
        // 在用户上等待下一次输入
        return getPrivateTask().nextInput(waitTime, onTimeout);
    }

    default String nextPrivateInput(Function<Void, Void> onTimeout) {
        return nextPrivateInput(MAX_WAIT_TIME, onTimeout);
    }

    default String nextPrivateInput() {
        return nextPrivateInput(MAX_WAIT_TIME, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextPrivateInput(long maxWaitTime) {
        return nextPrivateInput(maxWaitTime, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextGroupInput(long group, long waitTime, Function<Void, Void> onTimeout) {
        // 在用户上等待下一次输入
        return getGroupTask(group).nextInput(waitTime, onTimeout);
    }

    default String nextGroupInput(long group, Function<Void, Void> onTimeout) {
        return nextGroupInput(group, MAX_WAIT_TIME, onTimeout);
    }

    default String nextGroupInput(long group) {
        return nextGroupInput(group, MAX_WAIT_TIME, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextGroupInput(long group, long maxWaitTime) {
        return nextGroupInput(group, maxWaitTime, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextTempInput(long group, long waitTime, Function<Void, Void> onTimeout) {
        // 在用户上等待下一次输入
        return getTempTask(group).nextInput(waitTime, onTimeout);
    }

    default String nextTempInput(long group, Function<Void, Void> onTimeout) {
        return nextTempInput(group, MAX_WAIT_TIME, onTimeout);
    }

    default String nextTempInput(long group) {
        return nextTempInput(group, MAX_WAIT_TIME, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextTempInput(long group, long maxWaitTime) {
        return nextTempInput(group, maxWaitTime, para -> {
            sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    /**
     * 获得当前用户的群聊身份
     * @return 返回群聊用户的引用。失败时返回 {@code null}
     */
    default Member getAsGroupMember() {
        final ReceptionTask task = getReceptionTask();
        if (task.inGroup()) {
            return task.getMember();
        } else {
            return null;
        }
    }

    default boolean inGroup() {
        return getReceptionTask().inGroup();
    }

    /**
     * 获得当前用户的临时通话身份
     * @return 返回通话身份的引用。失败时返回 {@code null}
     */
    default Member getAsTempMember() {
        final ReceptionTask task = getReceptionTask();
        if (task.inTemp()) {
            return task.getMember();
        } else {
            return null;
        }
    }

    default boolean inTemp() {
        return getReceptionTask().inTemp();
    }

    /**
     * 获得当前用户的私聊身份
     * @return 返回私聊会话的引用。失败时返回 {@code null}
     */
    default Friend getAsPrivate() {
        final ReceptionTask task = getReceptionTask();
        if (task.inPrivate()) {
            return task.getFriend();
        } else {
            return null;
        }
    }

    default boolean inPrivate() {
        return getReceptionTask().inPrivate();
    }

    /**
     * 获取用户所在的群
     * @return 如果是群聊用户或临时会话，返回其所在群，否则返回 {@code null}
     */
    default Group getGroup() {
        final Member member = getReceptionTask().getMember();
        if (Objects.nonNull(member)) {
            return member.getGroup();
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
    default String getName() {
        final Account account = getAccount();
        if (Objects.nonNull(account) && Objects.nonNull(account.getAlias())) {
            return account.getAlias();
        } else {
            final ReceptionTask task = getReceptionTask();
            if (Objects.nonNull(task.getMember())) {
                return task.getMember().getNameCard();
            } else {
                return task.getFriend().getNick();
            }
        }
    }

    /**
     * 获取用户全名。包含所在的群的名称。
     * @return 用户全名
     */
    default String getCompleteName() {
        final Group group = getGroup();
        if (inGroup()) {
            return "[" + group.getName() + "(" + group.getId() + ")] " + getName() + "(" + getQQ() + ")";
        } else if (inTemp()) {
            return getName() + "(" + getQQ() + ")" + " 来自 [" + group.getName() + "(" + group.getId() + ")]";
        } else {
            return getName() + "(" + getQQ() + ")";
        }
    }

    default String getMessage() {
        return getReceptionTask().getMessage();
    }

    default List<String> getRecentMessages() {
        return getReceptionTask().getRecentMessage();
    }

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

    default ResponseGroup getResponseGroup() {
        final Group group = getGroup();
        if (Objects.nonNull(group)) {
            return getXiaomingBot().getResponseGroupManager().forCode(group.getId());
        } else {
            return null;
        }
    }

    Map<Long, List<String>> getRecentGroupMessages();

    default List<String> getRecentGroupMessages(long group) {
        return getRecentGroupMessages().get(group);
    }

    default List<String> getOrPutRecentGroupMessages(long group) {
        List<String> list = getRecentGroupMessages(group);
        if (Objects.isNull(list)) {
            list = new ArrayList<>();
            getRecentGroupMessages().put(group, list);
        }
        return list;
    }

    default String getGroupMessage() {
        return getReceptionTask().getMessage();
    }

    default String getGroupMessage(long group) {
        final List<String> list = getRecentGroupMessages(group);
        if (Objects.nonNull(list) && !list.isEmpty()) {
            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    default void setGroupMessage(long group, String message) {
        final List<String> list = getOrPutRecentGroupMessages(group);
        list.add(message);
        synchronized (list) {
            list.notifyAll();
        }
    }

    Map<Long, List<String>> getRecentTempMessages();

    default List<String> getRecentTempMessages(long group) {
        return getRecentTempMessages().get(group);
    }

    default List<String> getOrPutRecentTempMessages(long group) {
        List<String> list = getRecentTempMessages(group);
        if (Objects.isNull(list)) {
            list = new ArrayList<>();
            getRecentTempMessages().put(group, list);
        }
        return list;
    }

    default String getTempMessages(long group) {
        final List<String> list = getRecentTempMessages(group);
        if (Objects.nonNull(list) && !list.isEmpty()) {
            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    default void setTempMessage(long group, String message) {
        final List<String> list = getOrPutRecentTempMessages(group);
        list.add(message);
        synchronized (list) {
            list.notifyAll();
        }
    }

    List<String> getRecentPrivateMessage();

    default String getPrivateMessage() {
        final List<String> list = getRecentPrivateMessage();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    default void setPrivateMessage(String message) {
        final List<String> list = getRecentPrivateMessage();
        list.add(message);
        synchronized (list) {
            list.notifyAll();
        }
    }

    default ReceptionTask getReceptionTask() {
        final ReceptionTask task = getReceptionist().getReceptionTasks().getOrDefault(Thread.currentThread().getName(), getExternalTask());
        if (Objects.isNull(task)) {
            final Thread thread = Thread.currentThread();
            getLog().error("未知的线程获得一个调度任务：" + thread);
        }
        return task;
    }

    default ReceptionTask getGroupTask(long group) {
        return getReceptionist().getGroupTask(group);
    }

    default ReceptionTask getPrivateTask() {
        return getReceptionist().getPrivateTask();
    }

    default ReceptionTask getTempTask(long group) {
        return getReceptionist().getTempTask(group);
    }

    default ReceptionTask getExternalTask() {
        return getReceptionist().getExternalTask();
    }
}