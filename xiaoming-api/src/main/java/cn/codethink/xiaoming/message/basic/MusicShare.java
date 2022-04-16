package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.MessageCodeBuilder;
import lombok.Data;

/**
 * 分享音乐
 *
 * @author Chuanwise
 */
@Data
public class MusicShare
    extends AbstractBasicMessage {
    
    /**
     * 音乐软件类型
     */
    private final MusicSoftwareType softwareType;
    
    /**
     * 卡片标题
     */
    private final String title;
    
    /**
     * 卡片描述
     */
    private final String description;
    
    /**
     * 点击后跳转的页面
     */
    private final String jumpUrl;
    
    /**
     * 封面图片 URL
     */
    private final String coverUrl;
    
    /**
     * 音乐文件 URL
     */
    private final String musicUrl;
    
    /**
     * 在消息列表中看到的内容
     */
    private final String summary;
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("music")
            .argument("share")
            .argument(softwareType.toString().toLowerCase())
            .argument(title)
            .argument(description)
            .argument(jumpUrl)
            .argument(coverUrl)
            .argument(musicUrl)
            .argument(summary)
            .build();
    }
    
    @Override
    public String serializeToSummary() {
        return null;
    }
}
