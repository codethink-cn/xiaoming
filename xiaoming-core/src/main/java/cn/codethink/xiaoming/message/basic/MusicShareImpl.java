package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.MusicShare
 */
@Data
public class MusicShareImpl
    extends AbstractBasicMessage
    implements MusicShare {
    
    private final MusicSoftwareType softwareType;
    
    private final String title;
    
    private final String description;
    
    private final String summary;
    
    private final String jumpUrl;
    
    private final String coverUrl;
    
    private final String musicUrl;
    
    public MusicShareImpl(MusicSoftwareType softwareType,
                          String title,
                          String description,
                          String summary,
                          String jumpUrl,
                          String coverUrl,
                          String musicUrl) {
    
        Preconditions.objectNonNull(softwareType, "software type");
        Preconditions.objectArgumentNonEmpty(title, "title");
        Preconditions.objectArgumentNonEmpty(description, "description");
        Preconditions.objectArgumentNonEmpty(summary, "summary");
        Preconditions.objectArgumentNonEmpty(jumpUrl, "jumpUrl");
        Preconditions.objectArgumentNonEmpty(coverUrl, "coverUrl");
        Preconditions.objectArgumentNonEmpty(musicUrl, "musicUrl");
        
        this.softwareType = softwareType;
        this.title = title;
        this.description = description;
        this.summary = summary;
        this.jumpUrl = jumpUrl;
        this.coverUrl = coverUrl;
        this.musicUrl = musicUrl;
    }
}
