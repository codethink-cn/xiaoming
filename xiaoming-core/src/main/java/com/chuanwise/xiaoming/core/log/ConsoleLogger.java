package com.chuanwise.xiaoming.core.log;

import com.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsoleLogger extends FilePreservableImpl {
    List<CommandRecord> commands = new LinkedList<>();

    public void addCommand(CommandRecord record) {
        commands.add(record);
    }
}
