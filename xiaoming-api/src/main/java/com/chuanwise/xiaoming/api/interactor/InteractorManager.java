package com.chuanwise.xiaoming.api.interactor;

import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;

import java.util.Map;
import java.util.Set;

public interface InteractorManager {
    void register(Interactor interactor, XiaomingPlugin plugin);

    Set<Interactor> getInteractors(XiaomingPlugin plugin);

    Set<Interactor> getOrPutInteractors(XiaomingPlugin plugin);

    Set<Interactor> getCoreInteractors();

    void setCoreInteractors(Set<Interactor> coreInteractors);

    Map<XiaomingPlugin, Set<Interactor>> getPluginInteractors();

    void setPluginInteractors(Map<XiaomingPlugin, Set<Interactor>> pluginInteractors);

    void denyCoreRegister();
}