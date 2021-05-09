package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.command.executor.CommandExecutor;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandResponseEvent extends XiaomingEvent {
    CommandExecutor commandExecutor;
    XiaomingPlugin xiaomingPlugin;
}
