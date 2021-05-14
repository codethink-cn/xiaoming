package com.chuanwise.xiaoming.core.response;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.mamoe.mirai.contact.Group;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 响应群管理器
 */
@Getter
public class ResponseGroupManagerImpl extends JsonFilePreservable implements ResponseGroupManager {
    Set<ResponseGroupImpl> groups = new CopyOnWriteArraySet<>();

    @Setter
    transient XiaomingBot xiaomingBot;

    public Set<ResponseGroup> getGroups() {
        return (Set) groups;
    }

    public ResponseGroupManagerImpl() {}
}
