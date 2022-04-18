package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.resource.Resource;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>资源图片</h1>
 *
 * <p>通过某种资源指代的图片。如本地文件、远程 Url 等。</p>
 *
 * <p>消息码：{@code [image:resource,$value...]}</p>
 *
 * @author Chuanwise
 */
public interface ResourceImage
    extends Image, AutoSerializable, AutoSummarizable {
    
    /**
     * 由资源创建一个资源图片
     *
     * @param resource 资源
     * @return 资源图片
     * @throws NullPointerException resource 为 null
     */
    static ResourceImage of(Resource resource) {
        return XiaoMing.get().newResourceImage(resource);
    }
    
    /**
     * 获取图片对应的资源
     *
     * @return 图片对应的资源
     */
    Resource getResource();
}
