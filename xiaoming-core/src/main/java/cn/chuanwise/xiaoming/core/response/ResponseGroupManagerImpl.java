package cn.chuanwise.xiaoming.core.response;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.response.ResponseGroup;
import cn.chuanwise.xiaoming.api.response.ResponseGroupManager;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 响应群管理器
 */
@Getter
public class ResponseGroupManagerImpl extends FilePreservableImpl implements ResponseGroupManager {
    Set<ResponseGroupImpl> groups = new CopyOnWriteArraySet<>();

    @Setter
    transient XiaomingBot xiaomingBot;

    @Override
    public Set<ResponseGroup> getGroups() {
        return (Set) groups;
    }

    @Override
    public ResponseGroup addGroup(long group, String alias) {
        ResponseGroup responseGroup = new ResponseGroupImpl(group, alias);
        return addGroup(responseGroup);
    }

    public void setGroups(Set<ResponseGroupImpl> groups) {
        this.groups = groups;
        for (ResponseGroupImpl group : groups) {
            group.setXiaomingBot(getXiaomingBot());
            group.addTag("recorded");
            group.addTag(String.valueOf(group.code));
        }
    }

    public ResponseGroupManagerImpl() {}
}
