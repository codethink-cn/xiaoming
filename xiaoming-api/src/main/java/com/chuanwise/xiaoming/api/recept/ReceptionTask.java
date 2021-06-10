package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ReceptionTask extends HostObject, Runnable {
    int NO_RECEIPT_TIME = 3;
    long RECEIPT_PERIOD = TimeUnit.MINUTES.toMillis(5);
    long NEXT_INPUT_MAX_WAIT_TIME = TimeUnit.MINUTES.toMillis(5);

    /**
     * 获得接待的用户
     * @return
     */
    XiaomingUser getUser();

    String getIdentify();

    Receptionist getReceptionist();

    Thread getThread();

    default void onNextInput(Message message) {
        getUser().onNextInput(message);
    }

    boolean isBusy();

    List<? extends Message> getRecentMessages();
}
