package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.QuoteReply;

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;

public interface Message extends XiaomingObject, Cloneable {
    /**
     * 获取消息概要（会压缩信息显示。所有的图片变为 [图片] 等）
     * @return 消息概要字符串
     */
    String summary();

    String serialize();

    MessageChain getMessageChain();

    MessageChain getOriginalMessageChain();

    void setOriginalMessageChain(MessageChain messageChain);

    void setMessageChain(MessageChain messageChain);

    XiaomingContact getContact();

    long getTime();

    XiaomingUser getSender();

    default void saveResources() throws IOException {
        getXiaomingBot().getResourceManager().saveResources(this);
    }

    default ScheduableTask<Boolean> asyncSaveResources() {
        return getXiaomingBot().getScheduler().run(() -> {
            saveResources();
            return true;
        });
    }

    Message clone() throws CloneNotSupportedException;

    /** 撤回消息 */
    default boolean recall() {
        try {
            MessageSource.recall(getOriginalMessageChain());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 一段时间吼撤回消息
     * @param delay 延迟
     * @return 撤回消息的异步结果
     */
    default ScheduableTask<Boolean> recallLater(long delay) {
        return getXiaomingBot().getScheduler().runLater(delay, this::recall);
    }
}