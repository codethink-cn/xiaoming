package com.chuanwise.xiaoming.api.util;

import lombok.Getter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * 使用 MD5 加密数据的工具类
 * @author Chuanwise
 */
public class MD5Utils extends UnstaticUtils {
    final MessageDigest MD5;

    {
        MessageDigest MD;
        try {
            MD = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD = null;
            System.exit(-1);
        }
        MD5 = MD;
    }

    static class InstanceHolder {
        static final MD5Utils INSTANCE = new MD5Utils();
    }

    public static MD5Utils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public byte[] getMD5(byte[] inputs) {
        if (Objects.nonNull(MD5)) {
            return MD5.digest(inputs);
        } else {
            return inputs;
        }
    }

    public byte[] getMD5(String inputs) {
        return getMD5(inputs.getBytes());
    }
}
