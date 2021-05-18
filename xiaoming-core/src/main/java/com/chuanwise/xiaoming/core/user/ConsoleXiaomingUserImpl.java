package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl implements ConsoleXiaomingUser {
    public ConsoleXiaomingUserImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }
}
