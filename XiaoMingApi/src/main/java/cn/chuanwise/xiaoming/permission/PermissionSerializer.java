package cn.chuanwise.xiaoming.permission;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PermissionSerializer extends JsonSerializer<Permission> {
    @Override
    public void serialize(Permission permission, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(permission.toString());
    }
}
