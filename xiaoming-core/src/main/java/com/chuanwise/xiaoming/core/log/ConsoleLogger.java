package com.chuanwise.xiaoming.core.log;

import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import com.chuanwise.xiaoming.api.interactor.filter.ParameterFilterMatcher;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsoleLogger extends JsonFilePreservable {
    List<CommandRecord> commands = new LinkedList<>();

    public void addCommand(CommandRecord record) {
        commands.add(record);
    }
}
