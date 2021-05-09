package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.event.UserInteractor;
import com.chuanwise.xiaoming.api.exception.InteactorTimeoutException;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface XiaomingUser extends XiaomingObject {
    /**
     * 给当前使用者发普通消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    boolean sendMessage(String message, Object... arguments);

    /**
     * 给当前使用者发错误消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    boolean sendError(String message, Object... arguments);

    /**
     * 给当前使用者发警告消息
     * @param message 消息。其中可用 {} 引用参数
     * @param arguments 实参
     * @return
     */
    boolean sendWarning(String message, Object... arguments);

    /**
     * 检查用户是否具有某个权限
     * @param node 权限节点
     * @return 查询结果
     */
    default boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    default boolean hasPermissions(String[] nodes) {
        for (String node : nodes) {
            if (!hasPermission(node)) {
                return false;
            }
        }
        return true;
    }

    default boolean checkPermissionAndReport(String node) {
        if (!hasPermission(node)) {
            sendError("小明不能帮你做这件事哦，因为你还没有权限：{}", node);
            return false;
        } else {
            return true;
        }
    }

    default String nextInput(long waitTime, Function<Void, Void> onTimeout) {
        return getUserInteractRunnable().getNextInput(waitTime, onTimeout);
    }

    default String nextInput(Function<Void, Void> onTimeout) {
        return getUserInteractRunnable().getNextInput(TimeUtil.MINUTE_MINS * 10, onTimeout);
    }

    default String nextInput() {
        return nextInput(TimeUtil.MINUTE_MINS * 10, para -> {
            sendMessage("你已经十分钟没有理小明啦，小明就不再等待你的下一条消息啦");
            throw new InteactorTimeoutException();
        });
    }

    long getQQ();

    String getMessage();

    UserInteractRunnable getUserInteractRunnable();

    void setUserInteractRunnable(UserInteractRunnable runnable);

    void setMessage(String message);

    default Account getAccount() {
        return getXiaomingBot().getAccountManager().getAccount(getQQ());
    }

    default Account getOrPutAccount() {
        return getXiaomingBot().getAccountManager().getOrPutAccount(getQQ());
    }

    void setUseBuffer(boolean useBuffer);

    boolean isUseBuffer();

    StringBuilder getBuffer();

    default void clearBuffer() {
        getBuffer().setLength(0);
    }

    default void useBuffer() {
        setUseBuffer(true);
        clearBuffer();
    }

    default String getBufferAndClose() {
        final String string = getBuffer().toString();
        clearBuffer();
        setUseBuffer(false);
        return string;
    }

    String getName();

    List<String> getRecentInputs();

    void setRecentInputs(List<String> lastInputs);

    /**
     * 设置最后一次的输入
     */
    default void clearRecentInputs() {
        setRecentInputs(new ArrayList<>());
    }

    UserInteractor getUserInteractor();

    void setUserInteractor(UserInteractor userInteractor);

    default boolean isBlockPlugin(String pluginName) {
        final Account account = getAccount();
        if (Objects.nonNull(account)) {
            return account.isBlockPlugin(pluginName);
        } else {
            return false;
        }
    }
}
