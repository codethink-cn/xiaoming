package cn.codethink.xiaoming.resource;

import java.io.InputStream;

/**
 * 表示某种资源
 *
 * @author Chuanwise
 */
public interface Resource {
    
    /**
     * 打开和该资源连接的二进制流。
     * 调用者应该负责关闭输入流。
     *
     * @return 输入流
     * @throws Exception 打开资源出现异常
     */
    InputStream open() throws Exception;
}
