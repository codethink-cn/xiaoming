package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.resource.Resource;

/**
 * <h1>文件</h1>
 *
 * <p>表示一个文件</p>
 *
 * @author Chuanwise
 */
public interface File
    extends SingletonMessage, AutoSerializable, AutoSummarizable {
    
//    static File of()
    
    /**
     * 获取文件资源
     *
     * @return 文件资源
     */
    Resource getResource();
    
    /**
     * 获取文件名
     *
     * @return 文件名
     */
    String getName();
    
    /**
     * 获取文件大小
     *
     * @return 文件大小
     */
    String getSize();
}
