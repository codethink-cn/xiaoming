package cn.chuanwise.xiaoming.object;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;

@Getter
public class ModuleObjectImpl extends XiaomingObjectImpl implements ModuleObject {
    public ModuleObjectImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    transient Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Transient
    @Override
    public Logger getLogger() {
        return logger;
    }
}
