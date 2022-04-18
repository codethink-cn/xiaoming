package cn.codethink.xiaoming.resource;

import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.spi.XiaoMing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 表示某种资源
 *
 * @author Chuanwise
 */
public interface Resource
    extends Serializable {
    
    /**
     * 将外部文件作为资源
     *
     * @param file 外部文件
     * @return 资源
     * @throws NullPointerException file 为 null
     */
    static Resource of(File file) {
        return XiaoMing.get().newResource(file);
    }
    
    /**
     * 用 url 创建资源
     *
     * @param url 外部文件
     * @return 资源
     * @throws NullPointerException url 为 null
     */
    static Resource of(URL url) {
        return XiaoMing.get().newResource(url);
    }
    
    /**
     * 将字节数组作为资源
     *
     * @param bytes 字节数组
     * @return 资源
     * @throws NullPointerException bytes 为 null
     */
    static Resource of(byte[] bytes) {
        return XiaoMing.get().newResource(bytes);
    }
    
    /**
     * 将类路径下的组件作为资源
     *
     * @param clazz 类
     * @param path  路径
     * @return 资源
     * @throws NullPointerException     clazz 或 path 为 null
     * @throws IllegalArgumentException path 为 ""
     */
    static Resource of(Class<?> clazz, String path) {
        return XiaoMing.get().newResource(clazz, path);
    }
    
    /**
     * 打开和该资源连接的二进制流。
     * 调用者应该负责关闭输入流。
     *
     * @return 输入流
     * @throws IOException 打开资源出现异常
     */
    InputStream open() throws IOException;
}