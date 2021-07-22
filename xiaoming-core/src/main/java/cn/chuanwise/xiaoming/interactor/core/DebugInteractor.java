package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.interactor.InteractorMethodInformation;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DebugInteractor extends InteractorImpl {
    public DebugInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @Data
    public static class Customizer extends FilePreservableImpl {
        Map<String, InteractorMethodInformation> methodInformation = new HashMap<>();
    }

    @Filter("debug1")
    public void onDebug1(XiaomingUser user) {
        final Set<Interactor> coreInteractors = getXiaomingBot().getInteractorManager().getCoreInteractors();
        final File directory = new File("customizer");
        directory.mkdirs();

        Consumer<Interactor> consumer = interactor -> {
            final File file = new File(directory, interactor.getClass().getSimpleName() + ".json");

            final Customizer customizer = getXiaomingBot().getFileLoader().loadOrSupplie(Customizer.class, file, Customizer::new);
            interactor.getInteractorMethodInformation().forEach(information -> customizer.methodInformation.put(information.getMethod().getName(), information));

            customizer.saveOrFail();
        };

        coreInteractors.forEach(consumer);
        getXiaomingBot().getInteractorManager().getPluginInteractors().values().forEach(set -> set.forEach(consumer));
    }
}
