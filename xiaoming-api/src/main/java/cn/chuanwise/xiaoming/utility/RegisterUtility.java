package cn.chuanwise.xiaoming.utility;

import cn.chuanwise.api.Registrable;
import cn.chuanwise.utility.CheckUtility;
import cn.chuanwise.utility.StaticUtility;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class RegisterUtility extends StaticUtility {
    public static void checkRegister(XiaomingBot xiaomingBot, Plugin plugin, String objectName) {
        CheckUtility.checkState(xiaomingBot.getStatus() != XiaomingBot.Status.ENABLED || Objects.nonNull(plugin),
                "can not register " + objectName + " as xiaoming core");
    }

    public static void checkUnregister(XiaomingBot xiaomingBot, Plugin plugin, String objectName) {
        CheckUtility.checkState(xiaomingBot.getStatus() != XiaomingBot.Status.ENABLED || Objects.nonNull(plugin),
                "can not unregister " + objectName + "s registered by core");
    }

    public static <T extends Registrable> void unregister(Collection<T> collection, Predicate<T> filter) {
        collection.removeIf(element -> {
            final boolean result = filter.test(element);
            if (result) {
                element.onUnregister();
            }
            return result;
        });
    }

    public static <T extends Registrable> void register(Collection<T> collection, T... elements) {
        for (T element : elements) {
            collection.add(element);
            element.onRegister();
        }
    }
}