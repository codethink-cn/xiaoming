package cn.chuanwise.xiaoming.account.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberCommandRecord extends CommandRecord {
    long group;

    public MemberCommandRecord(long group, String command) {
        super(command);
        this.group = group;
    }
}
