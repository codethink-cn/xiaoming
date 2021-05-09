package com.chuanwise.xiaoming.core.permission;

import com.chuanwise.xiaoming.api.event.EventListenerManager;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.permission.PermissionGroup;
import com.chuanwise.xiaoming.core.event.EventListenerImpl;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Data;
import net.mamoe.mirai.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionGroupImpl implements PermissionGroup {
    private List<String> superGroups = new ArrayList<>();
    private String alias;
    private List<String> permissions = new ArrayList<>();

    @Override
    public void addPermission(String node) {
        permissions.add(node);
    }

    @Override
    public void removePermission(String node) {
        permissions.remove(node);
    }
}