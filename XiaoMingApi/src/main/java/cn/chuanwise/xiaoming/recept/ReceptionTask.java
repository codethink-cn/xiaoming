package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface ReceptionTask<U extends XiaomingUser<?>> extends ModuleObject, Callable<Boolean> {
    U getUser();

    Thread getThread();

    boolean isBusy();
}
