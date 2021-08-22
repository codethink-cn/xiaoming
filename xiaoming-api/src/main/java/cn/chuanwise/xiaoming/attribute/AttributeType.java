package cn.chuanwise.xiaoming.attribute;

import cn.chuanwise.toolkit.verify.VerifyCodeManager;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AttributeType<T> {
    protected static final Map<String, AttributeType> INSTANCES = new HashMap<>();

    public static final AttributeType<Long> QQ = new AttributeType<>("qq");
    public static final AttributeType<String> AT = new AttributeType<>("at");
    public static final AttributeType<Message> LAST = new AttributeType<>("last");
    public static final AttributeType<GroupContact> GROUP = new AttributeType<>("group");
    public static final AttributeType<Map<String, String>> ARGUMENTS = new AttributeType<>("arguments");
    public static final AttributeType<VerifyCodeManager> VERIFY_CODE_MANAGER = new AttributeType<>("verify-code-manager");

    public static AttributeType<?> valueOf(String identifier) {
        return INSTANCES.get(identifier);
    }

    final String identifier;

    public AttributeType() {
        this.identifier = null;
    }

    public AttributeType(String identifier) {
        this.identifier = identifier;
    }

    public T get(AttributeHolder holder) {
        return holder.getAttribute(this);
    }

    public void set(AttributeHolder holder, T value) {
        holder.setAttribute(this, value);
    }
}