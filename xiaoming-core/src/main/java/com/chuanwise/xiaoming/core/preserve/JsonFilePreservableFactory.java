package com.chuanwise.xiaoming.core.preserve;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.util.JsonSerializerUtil;

import java.io.*;

/**
 * @see com.chuanwise.xiaoming.api.preserve.PreservableFactory
 * @author Chuanwise
 */
public class JsonFilePreservableFactory implements PreservableFactory<File> {
    @Override
    public <T extends Preservable<File>> T loadThrowsException(Class<T> clazz, File medium) throws IOException {
        T result = null;
        if (!medium.isFile()) {
            return result;
        }
        try (InputStream inputStream = new FileInputStream(medium)) {
            result = JsonSerializerUtil.getINSTANCE().readValue(inputStream, clazz);
        }
        return result;
    }
}
