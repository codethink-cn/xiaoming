package com.chuanwise.xiaoming.core.preserve;

import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.util.JsonSerializerUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
            JsonSerializerUtil.getINSTANCE().writeValue(outputStream, this);
        }
        return true;
    }
}