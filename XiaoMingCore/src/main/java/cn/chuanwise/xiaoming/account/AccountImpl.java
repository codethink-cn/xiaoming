package cn.chuanwise.xiaoming.account;

import cn.chuanwise.api.AbstractOriginalTagMarkable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountImpl
        extends AbstractOriginalTagMarkable
        implements Account {
    long code;
    String alias;
    boolean administrator;
    boolean banned;
}
