package com.chuanwise.xiaoming.core.object;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class HostXiaomingObjectImpl extends XiaomingObjectImpl implements HostXiaomingObject {
    /**
     * 日志类
     */
    Logger log = LoggerFactory.getLogger(getClass());

    public HostXiaomingObjectImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }
}
