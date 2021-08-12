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
public class GroupRecordImpl extends XiaomingObjectImpl implements GroupRecord {
    long code;
    @Setter
    String alias;

    Set<String> tags = new CopyOnWriteArraySet<>();

    public GroupRecordImpl(long code, String alias) {
        this.code = code;
        this.alias = alias;
    }

    public GroupRecordImpl(long code) {
        this.code = code;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
        tags.addAll(Arrays.asList(RECORDED, String.valueOf(code)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupRecordImpl that = (GroupRecordImpl) o;
        return code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
