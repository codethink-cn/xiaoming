package com.chuanwise.xiaoming.example;

import com.chuanwise.xiaoming.core.plugin.XiaomingPluginImpl;
import com.chuanwise.xiaoming.example.interactor.InteractorExample;

/**
 * 插件主类范例
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {
    @Override
    public void onEnable() {
        getXiaomingBot().getInteractorManager().register(new InteractorExample(), this);
    }
}
