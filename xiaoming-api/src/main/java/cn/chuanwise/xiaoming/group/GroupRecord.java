package cn.chuanwise.xiaoming.group;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.tag.PluginBlockable;

import java.util.Set;

public interface GroupRecord extends XiaomingObject, PluginBlockable {
    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    String getAlias();

    void setAlias(String alias);

    default String getAliasAndCode() {
        final String alias = getAlias();
        if (StringUtil.isEmpty(alias)) {
            return getCodeString();
        } else {
            return alias + "（" + getCodeString() + "）";
        }
    }

    default GroupContact getContact() {
        return getXiaomingBot().getContactManager().getGroupContact(getCode());
    }

    @Override
    default Set<String> getOriginalTags() {
        return CollectionUtil.asSet(getCodeString(), RECORDED);
    }
}