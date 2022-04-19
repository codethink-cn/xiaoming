package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 音频编码，可由 {@link OnlineAudio#getCodec()} 获得。
 *
 * @author Chuanwise
 */
public enum AudioCodec {
    
    /**
     * 低品质
     */
    AMR("amr"),
    
    /**
     * 高品质
     */
    SILK("silk");
    
    /**
     * 编码名
     */
    private final String name;
    
    /**
     * 所有实例的哈希表
     */
    private static final Map<String, AudioCodec> NAME_INSTANCES = new HashMap<>();
    
    static {
        for (AudioCodec value : values()) {
            NAME_INSTANCES.put(value.name, value);
        }
    }
    
    AudioCodec(String name) {
        this.name = name;
    }
    
    /**
     * 通过编码名获取编码
     *
     * @param name 编码名
     * @return 编码
     */
    public static AudioCodec of(String name) {
        Preconditions.objectArgumentNonEmpty(name, "name");
    
        return Maps.getOrFail(NAME_INSTANCES, name);
    }
}
