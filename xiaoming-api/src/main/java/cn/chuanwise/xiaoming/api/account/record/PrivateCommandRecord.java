package cn.chuanwise.xiaoming.api.account.record;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PrivateCommandRecord extends CommandRecord {
    public PrivateCommandRecord(String command) {
        super(command);
    }
}