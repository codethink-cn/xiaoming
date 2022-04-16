package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.annotation.InternalAPI;

/**
 * 通过缓存实现的，可能会失效的对象
 *
 * @author Chuanwise
 */
@InternalAPI
public interface Cached {
    
    /**
     * 询问对象是否仍然有效
     *
     * @return 对象是否仍然有效
     */
    boolean isAvailable();
}
