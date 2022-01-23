package cn.chuanwise.xiaoming.group;

import cn.chuanwise.xiaoming.object.XiaomingObjectImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 响应群的相关信息
 * @author Chuanwise
 */
@Getter
@NoArgsConstructor
public class GroupInformationImpl extends XiaomingObjectImpl implements GroupInformation {
    long code;
    @Setter
    String alias;

    Set<String> tags = new CopyOnWriteArraySet<>();

    public GroupInformationImpl(long code, String alias) {
        this.code = code;
        this.alias = alias;
    }

    public GroupInformationImpl(long code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupInformationImpl that = (GroupInformationImpl) o;
        return code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public void flush() {
        tags.addAll(getOriginalTags());
    }

    @Override
    public boolean addTag(String tag) {
        return tags.add(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }
}
