package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.resource.Resource;

import java.net.URL;

/**
 * 图片消息
 *
 * @author Chuanwise
 */
public interface Image
    extends BasicMessage, Resource, SpacedMessage {
    
    /**
     * 获取图片的 URL
     *
     * @return 图片的 URL
     */
    String getUrlString();
    
    /**
     * 获取图片的 URL
     *
     * @return 图片的 URL
     */
    URL getUrl();
    
    @Override
    @SuppressWarnings("all")
    default String serializeToSummary() {
        return "[图片]";
    }
}

