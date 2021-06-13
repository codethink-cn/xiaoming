package com.chuanwise.xiaoming.core.object;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class ModuleObjectImpl extends XiaomingObjectImpl implements ModuleObject {
    public ModuleObjectImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Logger getLog() {
        return log;
    }
}
