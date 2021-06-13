package com.chuanwise.xiaoming.api.account.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Record {
    long time = System.currentTimeMillis();

    public String getMessage() {
        return "（无描述）";
    }
}