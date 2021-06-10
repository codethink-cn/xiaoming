package com.chuanwise.xiaoming.core.preserve;

import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.util.JsonSerializerUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 可以通过 json 文件保存的数据
 * @author Chuanwise
 */
public class JsonFilePreservable implements Preservable<File> {
    transient File file;

    @Override
    public File getMedium() {
        return file;
    }

    @Override
    public void setMedium(File file) {
        this.file = file;
    }

    @Override
    public boolean saveToThrowsException(File file) throws IOException {
        // 不是文件且创建失败时返回 false
        if (!file.isFile() && !file.createNewFile()) {
            return false;
        }
        // 写入本对象的 json 数据
        try (OutputStream outputStream = new FileOutputStream(file)) {
            JsonSerializerUtils.getINSTANCE().writeValue(outputStream, this);
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonFilePreservable)) {
            return false;
        }
        JsonFilePreservable that = (JsonFilePreservable) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}