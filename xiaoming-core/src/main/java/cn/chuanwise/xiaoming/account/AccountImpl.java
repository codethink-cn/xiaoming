package cn.chuanwise.xiaoming.account;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.account.record.Record;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class AccountImpl extends FilePreservableImpl implements Account {
    long code;
    String alias;

    List<Record> events = new ArrayList<>();
    List<Record> histories = new ArrayList<>();
    List<CommandRecord> commands = new ArrayList<>();

    Set<String> tags = new HashSet<>();

    public void setTags(Set<String> tags) {
        this.tags = tags;
        tags.addAll(Arrays.asList(String.valueOf(code), "recorded"));
    }
}
