package com.chuanwise.xiaoming.api.preserve;

import com.chuanwise.xiaoming.api.object.XiaomingObject;

import java.io.IOException;

/**
 * 可以保存的数据
 * @author Chuanwise
 */
public interface Preservable<Medium> {
    /**
     * 保存当前数据到当前存储介质
     * @return 保存是否成功
     */
    default boolean save() {
        try {
            return saveTo(getMedium());
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 保存当前数据到指定存储介质
     * @param medium 存储介质
     * @return 保存是否成功
     */
    default boolean saveTo(Medium medium) {
        try {
            return saveToThrowsException(medium);
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获取当前的存储介质
     * @return 当前存储介质
     */
    Medium getMedium();

    /**
     * 更改当前的存储介质
     * @param medium
     */
    void setMedium(Medium medium);

    /**
     * 保存当前数据到当前存储介质
     * @return 保存是否成功
     * @exception IOException 保存失败时的异常
     */
    default boolean saveThrowsException() throws IOException {
        return saveToThrowsException(getMedium());
    }

    /**
     * 保存当前数据到指定存储介质
     * @param medium 存储介质
     * @return 保存是否成功
     * @exception IOException 保存失败时的异常
     */
    boolean saveToThrowsException(Medium medium) throws IOException;
}
