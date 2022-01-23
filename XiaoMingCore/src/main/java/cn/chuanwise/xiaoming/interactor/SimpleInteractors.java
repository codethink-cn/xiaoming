package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.xiaoming.object.PluginObjectImpl;
import cn.chuanwise.xiaoming.plugin.Plugin;

import lombok.Data;

/**
 * 交互器标准实现
 * @author Chuanwise
 */
@Data
public class SimpleInteractors<T extends Plugin>
        extends PluginObjectImpl<T>
        implements Interactors<T> {
}