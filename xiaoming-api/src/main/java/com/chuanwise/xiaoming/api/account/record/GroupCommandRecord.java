package com.chuanwise.xiaoming.api.account.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCommandRecord extends CommandRecord {
    long group;

    public GroupCommandRecord(long group, String command) {
        super(command);
        this.group = group;
    }
}