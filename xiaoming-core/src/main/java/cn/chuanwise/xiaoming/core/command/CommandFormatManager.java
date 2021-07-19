package cn.chuanwise.xiaoming.core.command;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.command.CommandFormat;
import cn.chuanwise.xiaoming.api.object.ModuleObject;
import cn.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandFormatManager extends ModuleObjectImpl {
    Map<String, CommandFormat> commandFormats = new HashMap<>();

    public CommandFormatManager(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }
}
