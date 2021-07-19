package cn.chuanwise.xiaoming.api.utility;

import cn.chuanwise.utility.StaticUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileUtils extends StaticUtility {
    public static final int COPY_PACK_SIZE = 1024;

    public static boolean copyResource(String path, File to)
            throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(to);
             InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(path)) {
            return copyResource(inputStream, fileOutputStream);
        }
    }

    public static boolean copyResourceOrCreate(String path, File to)
            throws IOException {
        if (!to.isFile()) {
            to.createNewFile();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(to);
             InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(path)) {
            return copyResource(inputStream, fileOutputStream);
        }
    }

    public static boolean copyResource(InputStream inputStream, File to)
            throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(to)) {
            return copyResource(inputStream, fileOutputStream);
        }
    }

    public static boolean copyResource(InputStream inputStream, FileOutputStream fileOutputStream)
            throws IOException {
        byte[] bytes = new byte[COPY_PACK_SIZE];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, len);
        }
        return true;
    }

    public static boolean copyResource(ClassLoader classLoader, String path, File to)
            throws IOException {
        final InputStream inputStream = classLoader.getResourceAsStream(path);
        if (Objects.nonNull(inputStream)) {
            final boolean result = copyResource(inputStream, to);
            inputStream.close();
            return result;
        } else {
            return false;
        }
    }
}
