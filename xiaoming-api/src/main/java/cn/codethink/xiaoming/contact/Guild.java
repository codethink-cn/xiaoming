package cn.codethink.xiaoming.contact;

import java.util.List;

/**
 * 一对多的频道，只可以在子区域推送消息
 *
 * @author Chuanwise
 */
public interface Guild
        extends Mass {
    
    /**
     * 获取服务器下的频道
     *
     * @return 频道
     */
    List<Channel> getChannel();
}
