package com.chuanwise.xiaoming.core.object;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.HostObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Slf4j
public class HostObjectImpl extends XiaomingObjectImpl implements HostObject {
    public HostObjectImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public Logger getLog() {
        return log;
    }
}
