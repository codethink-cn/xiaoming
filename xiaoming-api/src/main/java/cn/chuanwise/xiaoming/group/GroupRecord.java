package cn.chuanwise.xiaoming.group;

import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.tag.PluginBlockable;
import cn.chuanwise.xiaoming.tag.TagHolder;

public interface GroupRecord extends XiaomingObject, PluginBlockable {
    @Override
    default String getName() {
        return getCodeString();
    }

    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    String getAlias();

    void setAlias(String alias);

    default String getAliasAndCode() {
        final String alias = getAlias();
        if (StringUtility.isEmpty(alias)) {
            return getCodeString();
        } else {
            return alias + "（" + getCodeString() + "）";
        }
    }

    default GroupContact getContact() {
        return getXiaomingBot().getContactManager().getGroupContact(getCode());
    }
}