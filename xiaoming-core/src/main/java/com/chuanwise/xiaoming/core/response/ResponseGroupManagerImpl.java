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
@NoArgsConstructor
public class ResponseGroupManagerImpl extends JsonFilePreservable implements ResponseGroupManager {
    Set<ResponseGroup> groups = new CopyOnWriteArraySet<>();

    @Setter
    transient XiaomingBot xiaomingBot;

    public ResponseGroupManagerImpl(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    @Override
    public ResponseGroup fromCode(long group) {
        for (ResponseGroup responseGroup : groups) {
            if (responseGroup.getCode() == group) {
                return responseGroup;
            }
        }
        return null;
    }

    @Override
    public void addGroup(ResponseGroup group) {
        groups.add(group);
    }

    public void sendMessageToTaggedGroup(String tag, String message) {
        for (ResponseGroup responseGroup : getXiaomingBot().getResponseGroupManager().fromTag("log")) {
            final Group group = getXiaomingBot().getMiraiBot().getGroup(responseGroup.getCode());
            if (Objects.nonNull(group)) {
                group.sendMessage(message);
            }
        }
    }
}
