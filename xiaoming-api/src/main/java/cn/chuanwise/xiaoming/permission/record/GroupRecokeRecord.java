package cn.chuanwise.xiaoming.permission.record;

import cn.chuanwise.xiaoming.account.record.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupRecokeRecord extends Record {
    String group;
    String node;
}
