package com.chuanwise.xiaoming.core.response;

import com.chuanwise.xiaoming.api.response.ResponseGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.mamoe.mirai.contact.Group;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 响应群的相关信息
 * @author Chuanwise
 */
@Getter
@NoArgsConstructor
public class ResponseGroupImpl implements ResponseGroup {
    long code;
    @Setter
    String alias;

    Set<String> blockedPlugins = new CopyOnWriteArraySet<>();

    Set<String> tags = new CopyOnWriteArraySet<>();

    public ResponseGroupImpl(long code, String alias) {
        this.code = code;
        this.alias = alias;
    }

    @Override
    public boolean isBlockPlugin(String pluginName) {
        return blockedPlugins.contains(pluginName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseGroupImpl that = (ResponseGroupImpl) o;
        return code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public void blockPlugin(String pluginName) {
        blockedPlugins.add(pluginName);
    }
}
