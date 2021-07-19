package cn.chuanwise.xiaoming.api.permission.record;

import cn.chuanwise.xiaoming.api.account.record.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserGrantRecord extends Record {
    long subject;
    String node;
}
