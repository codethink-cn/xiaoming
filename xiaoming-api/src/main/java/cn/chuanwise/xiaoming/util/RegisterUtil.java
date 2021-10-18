package cn.chuanwise.xiaoming.util;

import cn.chuanwise.api.Registrable;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.util.StaticUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

public class RegisterUtil extends StaticUtil {
    public static void checkRegister(XiaomingBot xiaomingBot, Plugin plugin, String objectName) {
        ConditionUtil.checkState(xiaomingBot.getStatus() != XiaomingBot.Status.ENABLED || Objects.nonNull(plugin),
                "can not register " + objectName + " as xiaoming core");
    }

    public static void checkUnregister(XiaomingBot xiaomingBot, Plugin plugin, String objectName) {
        ConditionUtil.checkState(xiaomingBot.getStatus() != XiaomingBot.Status.ENABLED || Objects.nonNull(plugin),
                "can not unregister " + objectName + "s registered by core");
    }

    public static <T extends Registrable> void register(Collection<T> collection, T... elements) {
        for (T element : elements) {
            collection.add(element);
            element.onRegister();
        }
    }
}