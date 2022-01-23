package cn.chuanwise.xiaoming.apply;

import cn.chuanwise.toolkit.verify.VerifyCodeHandler;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyHandler<T extends Plugin> extends VerifyCodeHandler implements PluginObject<T> {
    protected String[] permissions = new String[0];
    protected T plugin;
    protected XiaomingBot xiaomingBot;
    protected String message;
}
