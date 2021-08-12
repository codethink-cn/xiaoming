package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ReceptionTask extends ModuleObject, Runnable {
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
