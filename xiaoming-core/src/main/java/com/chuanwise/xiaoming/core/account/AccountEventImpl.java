package com.chuanwise.xiaoming.core.account;

import com.chuanwise.xiaoming.api.account.AccountEvent;
import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一个账户可能出现的事件
 * @author Chuanwise
 */
@Data
@NoArgsConstructor
public class AccountEventImpl implements AccountEvent {
    long time = System.currentTimeMillis();
    String message;
    long group;

    public AccountEventImpl(String message) {
        setMessage(message);
    }

    public AccountEventImpl(long group, String message) {
        setMessage(message);
        setGroup(group);
    }
}
