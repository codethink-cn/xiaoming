package com.chuanwise.xiaoming.api.permission.record;

import com.chuanwise.xiaoming.api.account.record.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupGrantRecord extends Record {
    String group;
    String node;
}
