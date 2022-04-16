package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 音乐软件类型，用于标记分享音乐 {@link MusicShare} 的来源。
 *
 * @author Chuanwise
 */
@Getter
@SuppressWarnings("all")
public enum MusicSoftwareType {
    
    /**
     * 网易云音乐
     */
    NETEASE_CLOUD_MUSIC("网易云音乐", "netease"),
    
    /**
     * QQ 音乐
     */
    QQ_MUSIC("QQ 音乐", "qq"),
    
    /**
     * 咪咕音乐
     */
    MIGU_MUSIC("咪咕音乐", "migu"),
    
    /**
     * 酷狗音乐
     */
    KUGOU_MUSIC("酷狗音乐", "kugou"),
    
    /**
     * 酷我音乐
     */
    KUWO_MUSIC("酷我音乐", "kuwo");
    
    /**
     * 软件名
     */
    private final String name;
    
    /**
     * 序列化名
     */
    private final String serializeName;
    
    /**
     * 序列化名表
     */
    private static final Map<String, MusicSoftwareType> SERIALIZE_NAME_MAP = new HashMap<>();
    
    static {
        for (MusicSoftwareType value : values()) {
            SERIALIZE_NAME_MAP.put(value.getSerializeName(), value);
        }
    }
    
    MusicSoftwareType(String name, String serializeName) {
        Preconditions.objectArgumentNonEmpty(name, "name");
        Preconditions.objectArgumentNonEmpty(serializeName, "serialize name");
        
        this.name = name;
        this.serializeName = serializeName;
    }
    
    /**
     * 通过序列化名获得软件类型
     *
     * @param serializeName 序列化名
     * @return 软件类型，或 null
     */
    public static MusicSoftwareType of(String serializeName) {
        Preconditions.objectArgumentNonEmpty(serializeName, "serialize name");

        return SERIALIZE_NAME_MAP.get(serializeName);
    }
}
