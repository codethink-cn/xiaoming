package cn.chuanwise.xiaoming.api.group;

import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.api.object.XiaomingObject;
import cn.chuanwise.xiaoming.api.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.api.tag.TagHolder;

import java.util.Set;

public interface GroupRecord extends XiaomingObject, TagHolder {
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