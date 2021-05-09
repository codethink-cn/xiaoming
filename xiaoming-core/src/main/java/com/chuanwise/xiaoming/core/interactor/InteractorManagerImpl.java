package com.chuanwise.xiaoming.core.interactor;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;
import lombok.Getter;

import java.util.*;


@Getter
public class InteractorManagerImpl extends HostXiaomingObjectImpl implements InteractorManager {
    Set<Interactor> coreInteractors = new HashSet<>();

    Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = new HashMap<>();

    public InteractorManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public void register(Interactor interactor, XiaomingPlugin plugin) {
        if (Objects.nonNull(plugin)) {
            getOrPutInteractors(plugin).add(interactor);
        } else {
            coreInteractors.add(interactor);
        }
        interactor.reloadInteractorDetails(getLog());
        interactor.setXiaomingBot(getXiaomingBot());
    }

    @Override
    public Set<Interactor> getInteractors(XiaomingPlugin plugin) {
        return pluginInteractors.get(plugin);
    }

    @Override
    public Set<Interactor> getOrPutInteractors(XiaomingPlugin plugin) {
        Set<Interactor> interactors = getInteractors(plugin);
        if (Objects.isNull(interactors)) {
            interactors = new HashSet<>();
            pluginInteractors.put(plugin, interactors);
        }
        return interactors;
    }

    @Override
    public Set<Interactor> getCoreInteractors() {
        return coreInteractors;
    }

    @Override
    public void setCoreInteractors(Set<Interactor> coreInteractors) {
        this.coreInteractors = coreInteractors;
    }

    @Override
    public Map<XiaomingPlugin, Set<Interactor>> getPluginInteractors() {
        return pluginInteractors;
    }

    @Override
    public void setPluginInteractors(Map<XiaomingPlugin, Set<Interactor>> pluginInteractors) {
        this.pluginInteractors = pluginInteractors;
    }

    @Override
    public void denyCoreRegister() {
        coreInteractors = Collections.unmodifiableSet(coreInteractors);
    }
}