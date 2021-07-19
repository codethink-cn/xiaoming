package cn.chuanwise.xiaoming.api.account.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class CommandRecord extends Record {
    String command;
}