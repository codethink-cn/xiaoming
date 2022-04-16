package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * mirai 的 vip 表情
 *
 * @author Chuanwise
 */
@Getter
@SuppressWarnings("all")
public enum VipFaceType {
    
    /**
     * 榴莲
     */
    LIU_LIAN(9, "榴莲"),
    
    /**
     * 平底锅
     */
    PING_DI_GUO(1, "平底锅"),
    
    /**
     * 钞票
     */
    CHAO_PIAO(12, "钞票"),
    
    /**
     * 略略略
     */
    LUE_LUE_LUE(10, "略略略"),
    
    /**
     * 猪头
     */
    ZHU_TOU(4, "猪头"),
    
    /**
     * 便便
     */
    BIAN_BIAN(6, "便便"),
    
    /**
     * 炸弹
     */
    ZHA_DAN(5, "炸弹"),
    
    /**
     * 爱心
     */
    AI_XIN(2, "爱心"),
    
    /**
     * 哈哈
     */
    HA_HA(3, "哈哈"),
    
    /**
     * 点赞
     */
    DIAN_ZAN(1, "点赞"),
    
    /**
     * 亲亲
     */
    QIN_QIN(7, "亲亲"),
    
    /**
     * 药丸
     */
    YAO_WAN(8, "药丸");
    
    /**
     * code -> VipFaceType
     */
    private static final Map<Integer, VipFaceType> INSTANCES = new HashMap<>();
    
    /**
     * 通过 vip face code 获得 VipFaceType
     *
     * @param code vip face code
     * @return VipFaceType
     * @throws java.util.NoSuchElementException 没有找到对应的 VipFaceType
     */
    public static VipFaceType of(int code) {
        final VipFaceType vipFaceType = INSTANCES.get(code);
        Preconditions.elementNonNull(vipFaceType);
        return vipFaceType;
    }
    
    /**
     * Vip 表情 id
     */
    private final int code;
    
    /**
     * Vip 表情名
     */
    private final String name;
    
    VipFaceType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}