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
    NETEASE_CLOUD_MUSIC("网易云音乐"),
    
    /**
     * QQ 音乐
     */
    QQ_MUSIC("QQ 音乐"),
    
    /**
     * 咪咕音乐
     */
    MIGU_MUSIC("咪咕音乐"),
    
    /**
     * 酷狗音乐
     */
    KUGOU_MUSIC("酷狗音乐"),
    
    /**
     * 酷我音乐
     */
    KUWO_MUSIC("酷我音乐");
    
    /**
     * 软件名
     */
    private final String name;
    
    MusicSoftwareType(String name) {
        Preconditions.objectArgumentNonEmpty(name, "name");
        
        this.name = name;
    }
}
