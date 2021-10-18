package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.toolkit.preservable.Preservable;

import java.io.File;
import java.util.List;

public interface PermissionSetting
        extends Preservable {
    List<String> getBasePermissions();
}
