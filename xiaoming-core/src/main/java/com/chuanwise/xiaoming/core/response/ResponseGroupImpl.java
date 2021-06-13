package com.chuanwise.xiaoming.core.response;

import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
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
public class ResponseGroupImpl extends XiaomingObjectImpl implements ResponseGroup {
    long code;
    @Setter
    String alias;

    Set<String> blockedPlugins = new CopyOnWriteArraySet<>();

    Set<String> tags = new CopyOnWriteArraySet<>();

    public ResponseGroupImpl(long code, String alias) {
        this.code = code;
        this.alias = alias;
    }

    public ResponseGroupImpl(long code) {
        this.code = code;
    }

    @Override
    public boolean isBlockPlugin(String pluginName) {
        return blockedPlugins.contains(pluginName);
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;

        // 检查原生标记
        final String recoededTag = "recorded";
        if (!tags.contains(recoededTag)) {
            tags.add(recoededTag);
        }

        final String codeTag = String.valueOf(code);
        if (!tags.contains(codeTag)) {
            tags.add(codeTag);
        }
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
