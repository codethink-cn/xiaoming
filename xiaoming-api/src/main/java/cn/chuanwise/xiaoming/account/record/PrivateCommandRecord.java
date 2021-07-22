package cn.chuanwise.xiaoming.account.record;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PrivateCommandRecord extends CommandRecord {
    public PrivateCommandRecord(String command) {
        super(command);
    }
}