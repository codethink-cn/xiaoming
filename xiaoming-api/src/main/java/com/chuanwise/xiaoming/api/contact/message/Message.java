package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.IOException;

public interface Message extends XiaomingObject {
    /**
     * 获取消息概要（会压缩信息显示。所有的图片变为 [图片] 等）
     * @return 消息概要字符串
     */
    String summary();

    /**
     * 序列化为 mirai 码
     * @return mirai 码
     */
    default String serialize() {
        return getMessageChain().serializeToMiraiCode();
    }

    MessageChain getMessageChain();

    void setMessageChain(MessageChain messageChain);

    XiaomingContact getContact();

    long getTime();

    XiaomingUser getSender();

    default void saveResources() throws IOException {
        getXiaomingBot().getResourceManager().saveResources(this);
    }

    default AsyncResult<Boolean> asyncSaveResources() {
        return getXiaomingBot().getScheduler().run(() -> {
            saveResources();
            return true;
        });
    }
}