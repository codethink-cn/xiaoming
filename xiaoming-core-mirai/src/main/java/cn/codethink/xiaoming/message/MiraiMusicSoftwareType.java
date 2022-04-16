package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.StaticUtilities;
import cn.codethink.xiaoming.message.basic.MusicSoftwareType;
import net.mamoe.mirai.message.data.MusicKind;

import java.util.NoSuchElementException;

/**
 * mirai 音乐软件类型
 *
 * @author Chuanwise
 */
public class MiraiMusicSoftwareType
    extends StaticUtilities {
    
    /**
     * 将 mirai 音乐软件类型转化为小明音乐软件类型
     *
     * @param musicKind mirai 音乐软件类型
     * @return 小明音乐软件类型
     * @throws NoSuchElementException 找不到音乐软件
     */
    public static MusicSoftwareType fromMirai(MusicKind musicKind) {
        switch (musicKind) {
            case QQMusic:
                return MusicSoftwareType.QQ_MUSIC;
            case KuwoMusic:
                return MusicSoftwareType.KUWO_MUSIC;
            case MiguMusic:
                return MusicSoftwareType.MIGU_MUSIC;
            case KugouMusic:
                return MusicSoftwareType.KUGOU_MUSIC;
            case NeteaseCloudMusic:
                return MusicSoftwareType.NETEASE_CLOUD_MUSIC;
            default:
                throw new NoSuchElementException();
        }
    }
    
    /**
     * 将小明音乐软件类型转化为 mirai 音乐软件类型
     *
     * @param musicSoftwareType 小明音乐软件类型
     * @return mirai 音乐软件类型
     * @throws NoSuchElementException 找不到音乐软件
     */
    public static MusicKind toMirai(MusicSoftwareType musicSoftwareType) {
        switch (musicSoftwareType) {
            case QQ_MUSIC:
                return MusicKind.QQMusic;
            case KUWO_MUSIC:
                return MusicKind.KuwoMusic;
            case MIGU_MUSIC:
                return MusicKind.MiguMusic;
            case KUGOU_MUSIC:
                return MusicKind.KugouMusic;
            case NETEASE_CLOUD_MUSIC:
                return MusicKind.NeteaseCloudMusic;
            default:
                throw new NoSuchElementException();
        }
    }
}