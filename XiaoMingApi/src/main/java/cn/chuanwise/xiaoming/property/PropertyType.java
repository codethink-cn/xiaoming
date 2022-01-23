package cn.chuanwise.xiaoming.property;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.verify.VerifyCodeManager;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import lombok.Getter;
import net.mamoe.mirai.message.data.At;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PropertyType<T> {
    protected static final Map<String, PropertyType> INSTANCES = new HashMap<>();

    public static final PropertyType<Long> QQ = new PropertyType<>("qq");
    public static final PropertyType<At> AT = new PropertyType<>("at");
    public static final PropertyType<Message> LAST = new PropertyType<>("last");
    public static final PropertyType<GroupContact> GROUP = new PropertyType<>("group");
    public static final PropertyType<Map<String, String>> ARGUMENTS = new PropertyType<>("arguments");
    public static final PropertyType<VerifyCodeManager> VERIFY_CODE_MANAGER = new PropertyType<>("verify-code-manager");

    public static PropertyType<?> valueOf(String identifier) {
        return INSTANCES.get(identifier);
    }

    final String identifier;

    public PropertyType() {
        this.identifier = null;
    }

    public PropertyType(String identifier) {
        this.identifier = identifier;
    }

    public Container<T> get(PropertyHandler handler) {
        return handler.getProperty(this);
    }

    public void set(PropertyHandler handler, T value) {
        handler.setProperty(this, value);
    }

    public Container<T> remove(PropertyHandler handler) {
        return handler.removeProperty(this);
    }
}