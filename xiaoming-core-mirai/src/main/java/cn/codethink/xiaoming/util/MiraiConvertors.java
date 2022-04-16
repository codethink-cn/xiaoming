package cn.codethink.xiaoming.util;

import cn.chuanwise.common.util.StaticUtilities;
import cn.codethink.xiaoming.message.basic.VipFaceType;
import net.mamoe.mirai.message.data.VipFace;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;

/**
 * 和 mirai 相关的组件的转换器
 *
 * @author Chuanwise
 */
public class MiraiConvertors
    extends StaticUtilities {
    
    ///////////////////////////////////////////////////////////////////////////
    // vip face
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * vip 表情
     */
    private static final BidiMap<VipFaceType, VipFace.Kind> VIP_FACES = new DualLinkedHashBidiMap<>();
    
    // initialize vip faces
    static {
        VIP_FACES.put(VipFaceType.LIU_LIAN, VipFace.LiuLian);
        VIP_FACES.put(VipFaceType.PING_DI_GUO, VipFace.PingDiGuo);
        VIP_FACES.put(VipFaceType.CHAO_PIAO, VipFace.ChaoPiao);
        VIP_FACES.put(VipFaceType.LUE_LUE_LUE, VipFace.LueLueLue);
        VIP_FACES.put(VipFaceType.ZHU_TOU, VipFace.ZhuTou);
        VIP_FACES.put(VipFaceType.BIAN_BIAN, VipFace.BianBian);
        VIP_FACES.put(VipFaceType.ZHA_DAN, VipFace.ZhaDan);
        VIP_FACES.put(VipFaceType.AI_XIN, VipFace.AiXin);
        VIP_FACES.put(VipFaceType.HA_HA, VipFace.HaHa);
        VIP_FACES.put(VipFaceType.DIAN_ZAN, VipFace.DianZan);
        VIP_FACES.put(VipFaceType.QIN_QIN, VipFace.QinQin);
        VIP_FACES.put(VipFaceType.YAO_WAN, VipFace.YaoWan);
    }
    
    /**
     * 将 mirai vip 表情类型转化为小明 vip 表情类型
     *
     * @param kind mirai vip 表情类型
     * @return 小明 vip 表情类型，或 null
     */
    public static VipFaceType fromMirai(VipFace.Kind kind) {
        return VIP_FACES.getKey(kind);
    }
    
    /**
     * 将小明 vip 表情类型转化为小明 vip 表情类型
     *
     * @param vipFaceType 小明 vip 表情类型
     * @return mirai vip 表情类型，或 null
     */
    public static VipFace.Kind toMirai(VipFaceType vipFaceType) {
        return VIP_FACES.get(vipFaceType);
    }
    
    
}