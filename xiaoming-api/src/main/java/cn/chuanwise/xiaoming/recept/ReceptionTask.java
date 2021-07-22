package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ReceptionTask extends ModuleObject, Runnable {
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
