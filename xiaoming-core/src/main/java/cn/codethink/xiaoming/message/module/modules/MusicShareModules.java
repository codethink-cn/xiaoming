package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.basic.MusicShare;
import cn.codethink.xiaoming.message.basic.MusicSoftwareType;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.SerializeContext;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;

import java.util.List;

/**
 * @see cn.codethink.xiaoming.message.basic.MusicShare
 *
 * @author Chuanwise
 */
public class MusicShareModules {
    
    @Summarizer(MusicShare.class)
    String summaryMusicShare(MusicShare musicShare) {
        return "[分享] " + musicShare.getTitle();
    }
    
    @Serializer(MusicShare.class)
    List<String> serializeMusicShare(SerializeContext context) {
        final MusicShare source = (MusicShare) context.getSource();
        
        return Collections.asUnmodifiableList(
            "music",
            "share",
            source.getSoftwareType().toString(),
            source.getTitle(),
            source.getDescription(),
            source.getSummary(),
            source.getJumpUrl(),
            source.getCoverUrl(),
            source.getMusicUrl()
        );
    }
    
    @Deserializer("music:share:?:?:?:?:?:?:?")
    MusicShare deserializeMusicShare(@DeserializerValue String softwareType,
                                     @DeserializerValue String title,
                                     @DeserializerValue String description,
                                     @DeserializerValue String summary,
                                     @DeserializerValue String jumpUrl,
                                     @DeserializerValue String coverUrl,
                                     @DeserializerValue String musicUrl) {
        
        return MusicShare.newInstance(
            MusicSoftwareType.valueOf(softwareType),
            title,
            description,
            summary,
            jumpUrl,
            coverUrl,
            musicUrl
        );
    }
}