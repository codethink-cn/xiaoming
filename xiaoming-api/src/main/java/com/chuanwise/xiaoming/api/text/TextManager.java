package com.chuanwise.xiaoming.api.text;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.util.StringUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public interface TextManager extends HostObject {
    default File textFile(String textName) {
        return new File(getDirectory(), textName + ".txt");
    }

    default String load(String textName) {
        return loadFrom(textFile(textName));
    }

    default String loadFrom(File file) {
        String result = null;

        if (file.isFile()) {
            try {
                byte[] bytes;
                try (InputStream inputStream = new FileInputStream(file)) {
                    bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);
                }
                result = new String(bytes);
            } catch (IOException exception) {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    default String loadOrFail(String textName) {
        final String result = load(textName);
        if (StringUtil.isEmpty(result)) {
            return textName;
        } else {
            return result;
        }
    }

    default String loadFromOrFail(File file) {
        final String result = loadFrom(file);
        if (StringUtil.isEmpty(result)) {
            return file.getName();
        } else {
            return result;
        }
    }

    default boolean saveThrowsException(String textName, String text) throws IOException {
        final File file = textFile(textName);
        if (!file.exists() && !file.createNewFile()) {
            return false;
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(text.getBytes());
        }
        return true;
    }

    default boolean save(String textName, String text) {
        try {
            return saveThrowsException(textName, text);
        } catch (IOException exception) {
            return false;
        }
    }

    default File[] list() {
        final File[] files = getDirectory().listFiles();
        final List<File> result = new ArrayList<>(files.length);

        for (File file : files) {
            if (file.getName().endsWith(".txt")) {
                result.add(file);
            }
        }
        return result.toArray(new File[0]);
    }

    File getDirectory();
}
