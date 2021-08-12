package cn.chuanwise.xiaoming.property;

import cn.chuanwise.utility.CheckUtility;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PropertyType<T> {
    protected static final Map<String, PropertyType> INSTANCES = new HashMap<>();

    public static final PropertyType<Long> QQ = new PropertyType<>("qq");
    public static final PropertyType<String> AT = new PropertyType<>("at");
    public static final PropertyType<Message> LAST = new PropertyType<>("last");
    public static final PropertyType<GroupContact> GROUP = new PropertyType<>("group");

    public static PropertyType<?> valueOf(String identifier) {
        return INSTANCES.get(identifier);
    }

    final String identifier;

    public PropertyType() {
        identifier = null;
    }

    public PropertyType(String identifier) {
        this.identifier = identifier;
        final PropertyType<?> sameIdentifierProperty = valueOf(identifier);

        CheckUtility.isNull(sameIdentifierProperty, "same name identifier property");
        INSTANCES.put(identifier, this);
    }

    public T get(PropertyHolder holder) {
        return holder.getProperty(this);
    }

    public void set(PropertyHolder holder, T value) {
        holder.setProperty(this, value);
    }
}