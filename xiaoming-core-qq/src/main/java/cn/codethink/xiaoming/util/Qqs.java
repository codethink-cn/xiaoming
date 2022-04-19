package cn.codethink.xiaoming.util;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;
import cn.chuanwise.common.util.StaticUtilities;
import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.basic.ImageCodec;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.message.metadata.*;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.protocol.Protocol;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.OfflineMessageSource;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.message.data.VipFace;
import net.mamoe.mirai.utils.BotConfiguration;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 和 qq 相关的组件的转换器
 *
 * @author Chuanwise
 */
public class Qqs
    extends StaticUtilities {
    
    ///////////////////////////////////////////////////////////////////////////
    // generic
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * 将键映射为值
     *
     * @param key 键
     * @param map 映射表
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 值
     * @throws NullPointerException   key 或 map 为 null
     * @throws NoSuchElementException 找不到对应的值
     */
    private static <K, V> V map(K key, Map<K, V> map) {
        Preconditions.objectNonNull(key, "key");
        Preconditions.objectNonNull(map, "map");
    
        return Maps.getOrFail(map, key);
    }
    
    /**
     * 将值映射为键
     *
     * @param value 值
     * @param map   映射表
     * @param <K>   键类型
     * @param <V>   值类型
     * @return 值
     * @throws NullPointerException   value 或 map 为 null
     * @throws NoSuchElementException 找不到对应的键
     */
    private static <K, V> K map(V value, BidiMap<K, V> map) {
        Preconditions.objectNonNull(value, "value");
        Preconditions.objectNonNull(map, "map");
    
        final K key = map.getKey(value);
        if (Objects.isNull(key)) {
            Preconditions.element(map.containsValue(value));
        }
        return key;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // vip face
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * vip 表情
     */
    private static final BidiMap<VipFaceType, VipFace.Kind> VIP_FACES = new DualLinkedHashBidiMap<>();
    
    // initialize mapper
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
     * 将 qq vip 表情类型转化为 vip 表情类型
     *
     * @param kind qq vip 表情类型
     * @return vip 表情类型，或 null
     * @throws NullPointerException             kind 为 null
     * @throws java.util.NoSuchElementException 没有对应的 vip 表情类型
     */
    public static VipFaceType toXiaoMing(VipFace.Kind kind) {
        return map(kind, VIP_FACES);
    }
    
    /**
     * 将小明 vip 表情类型转化为小明 vip 表情类型
     *
     * @param vipFaceType 小明 vip 表情类型
     * @return qq vip 表情类型
     * @throws NullPointerException             vipFaceType 为 null
     * @throws java.util.NoSuchElementException 没有对应的 qq vip 表情类型
     */
    public static VipFace.Kind toQq(VipFaceType vipFaceType) {
        return map(vipFaceType, VIP_FACES);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // image type
    ///////////////////////////////////////////////////////////////////////////
    
    private static final BidiMap<ImageCodec, net.mamoe.mirai.message.data.ImageType> IMAGE_TYPES = new DualLinkedHashBidiMap<>();
    
    // initialize mapper
    static {
        IMAGE_TYPES.put(ImageCodec.PNG, net.mamoe.mirai.message.data.ImageType.PNG);
        IMAGE_TYPES.put(ImageCodec.BMP, net.mamoe.mirai.message.data.ImageType.BMP);
        IMAGE_TYPES.put(ImageCodec.JPG, net.mamoe.mirai.message.data.ImageType.JPG);
        IMAGE_TYPES.put(ImageCodec.GIF, net.mamoe.mirai.message.data.ImageType.GIF);
        IMAGE_TYPES.put(ImageCodec.WEBP, net.mamoe.mirai.message.data.ImageType.APNG);
    }
    
    /**
     * 将 qq 图片类型转化为小明图片类型
     *
     * @param imageType qq 图片类型
     * @return 小明图片类型，或 null
     * @throws NullPointerException imageType 为 null
     */
    public static ImageCodec toXiaoMing(net.mamoe.mirai.message.data.ImageType imageType) {
        Preconditions.objectNonNull(imageType, "image type");
        
        return IMAGE_TYPES.getKey(imageType);
    }
    
    /**
     * 将小明图片类型转化为 qq 图片类型
     *
     * @param imageCodec qq 图片类型
     * @return qq 图片类型
     */
    public static net.mamoe.mirai.message.data.ImageType toQq(ImageCodec imageCodec) {
        if (Objects.isNull(imageCodec)) {
            return net.mamoe.mirai.message.data.ImageType.UNKNOWN;
        } else {
            return IMAGE_TYPES.get(imageCodec);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // music type
    ///////////////////////////////////////////////////////////////////////////
    
    private static final BidiMap<MusicSoftwareType, MusicKind> MUSIC_SOFTWARE_TYPES = new DualLinkedHashBidiMap<>();
    
    // initialize mapper
    static {
        MUSIC_SOFTWARE_TYPES.put(MusicSoftwareType.QQ_MUSIC, MusicKind.QQMusic);
        MUSIC_SOFTWARE_TYPES.put(MusicSoftwareType.KUWO_MUSIC, MusicKind.KuwoMusic);
        MUSIC_SOFTWARE_TYPES.put(MusicSoftwareType.MIGU_MUSIC, MusicKind.MiguMusic);
        MUSIC_SOFTWARE_TYPES.put(MusicSoftwareType.NETEASE_CLOUD_MUSIC, MusicKind.NeteaseCloudMusic);
        MUSIC_SOFTWARE_TYPES.put(MusicSoftwareType.KUGOU_MUSIC, MusicKind.KugouMusic);
    }
    
    /**
     * 将 qq 音乐软件类型转化为小明音乐软件类型
     *
     * @param musicKind qq 音乐软件类型
     * @return 小明音乐软件类型
     * @throws NullPointerException   musicKind 为 null
     * @throws NoSuchElementException 找不到音乐软件
     */
    public static MusicSoftwareType toXiaoMing(MusicKind musicKind) {
        return map(musicKind, MUSIC_SOFTWARE_TYPES);
    }
    
    /**
     * 将小明音乐软件类型转化为 qq 音乐软件类型
     *
     * @param musicSoftwareType 小明音乐软件类型
     * @return qq 音乐软件类型
     * @throws NullPointerException   musicSoftwareType 为 null
     * @throws NoSuchElementException 找不到音乐软件
     */
    public static MusicKind toQq(MusicSoftwareType musicSoftwareType) {
        return map(musicSoftwareType, MUSIC_SOFTWARE_TYPES);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * 将复合消息转化为 qq 消息链
     *
     * @param message    复合消息
     * @param properties 相关属性
     * @return qq 消息链
     * @throws NullPointerException   message 或 properties 为 null
     * @throws NoSuchElementException 存在无法转化的组件
     */
    public static MessageChain toQq(CompoundMessage message, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(properties, "properties");
    
        // basic message
        final List<SingleMessage> singleMessages = new ArrayList<>();
        for (BasicMessage basicMessage : message) {
            final SingleMessage singleMessage = MessageModule.convert(basicMessage, SingleMessage.class, properties);
            singleMessages.add(singleMessage);
        }
    
        // metadata
        for (cn.codethink.xiaoming.message.metadata.MessageMetadata messageMetadata : message.getMetadata().values()) {
            final SingleMessage singleMessage = MessageModule.convert(messageMetadata, SingleMessage.class, properties);
            singleMessages.add(singleMessage);
        }
    
        final MessageChainBuilder messageChainBuilder = new MessageChainBuilder(singleMessages.size());
        messageChainBuilder.addAll(singleMessages);
        return messageChainBuilder.asMessageChain();
    }
    
    /**
     * 将 qq 消息链转化为复合消息
     *
     * @param messageChain 消息链
     * @param properties   相关属性
     * @return 复合消息
     * @throws NullPointerException   messageChain 或 properties 为 null
     * @throws NoSuchElementException 存在无法转化的组件
     */
    public static CompoundMessage toXiaoMing(MessageChain messageChain, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(messageChain, "message chain");
        Preconditions.objectNonNull(properties, "properties");
    
        final CompoundMessageBuilder builder = CompoundMessageBuilder.newInstance();
        for (SingleMessage singleMessage : messageChain) {
            final Serializable serializable = MessageModule.convert(singleMessage, Serializable.class, properties);
    
            if (serializable instanceof BasicMessage) {
                final BasicMessage basicMessage = (BasicMessage) serializable;
                builder.plus(basicMessage);
                continue;
            }
    
            if (serializable instanceof cn.codethink.xiaoming.message.metadata.MessageMetadata) {
                final cn.codethink.xiaoming.message.metadata.MessageMetadata messageMetadata =
                    (cn.codethink.xiaoming.message.metadata.MessageMetadata) serializable;
                
                builder.plus(messageMetadata);
                continue;
            }
            
            throw new IllegalArgumentException("unknown single message: " + singleMessage);
        }
        
        return builder.build();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message source type
    ///////////////////////////////////////////////////////////////////////////
    
    private static final BidiMap<MessageSourceType, MessageSourceKind> MESSAGE_SOURCE_TYPES = new DualLinkedHashBidiMap<>();
    
    // initialize mapper
    static {
        MESSAGE_SOURCE_TYPES.put(MessageSourceType.FRIEND, MessageSourceKind.FRIEND);
        MESSAGE_SOURCE_TYPES.put(MessageSourceType.GROUP, MessageSourceKind.GROUP);
        MESSAGE_SOURCE_TYPES.put(MessageSourceType.STRANGER, MessageSourceKind.STRANGER);
        MESSAGE_SOURCE_TYPES.put(MessageSourceType.MEMBER, MessageSourceKind.TEMP);
    }
    
    /**
     * 通过多态性，转换 qq 消息源类型为小明消息源类型
     *
     * @param messageSource qq 消息源
     * @return 小明消息源类型
     * @throws NullPointerException   messageSource 为 null
     * @throws NoSuchElementException 没有对应的小明消息源类型
     */
    public static MessageSourceType toXiaoMing(MessageSource messageSource) {
        final MessageSourceType messageSourceType;
        if (messageSource instanceof OfflineMessageSource) {
            messageSourceType = toXiaoMing(((OfflineMessageSource) messageSource).getKind());
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromFriend
            || messageSource instanceof OnlineMessageSource.Outgoing.ToFriend) {
            messageSourceType = MessageSourceType.FRIEND;
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromGroup
            || messageSource instanceof OnlineMessageSource.Outgoing.ToGroup) {
            messageSourceType = MessageSourceType.GROUP;
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromTemp
            || messageSource instanceof OnlineMessageSource.Outgoing.ToTemp) {
            messageSourceType = MessageSourceType.MEMBER;
        } else if (messageSource instanceof OnlineMessageSource.Incoming.FromStranger
            || messageSource instanceof OnlineMessageSource.Outgoing.ToStranger) {
            messageSourceType = MessageSourceType.STRANGER;
        } else {
            throw new NoSuchElementException();
        }
        return messageSourceType;
    }
    
    /**
     * 转换 qq 消息源类型为小明消息源类型
     *
     * @param kind qq 消息源类型
     * @return 小明消息源类型
     * @throws NullPointerException   kind 为 null
     * @throws NoSuchElementException 没有对应的小明消息源类型
     */
    public static MessageSourceType toXiaoMing(MessageSourceKind kind) {
        return map(kind, MESSAGE_SOURCE_TYPES);
    }
    
    /**
     * 转换小明消息源类型为 qq 消息源类型
     *
     * @param type 小明消息源类型
     * @return qq 消息源类型
     * @throws NullPointerException   kind 为 null
     * @throws NoSuchElementException 没有对应的 qq 消息源类型
     */
    public static MessageSourceKind toQq(MessageSourceType type) {
        return map(type, MESSAGE_SOURCE_TYPES);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // primitive face
    ///////////////////////////////////////////////////////////////////////////
    
    private static final BidiMap<PrimitiveFace, Face> PRIMITIVE_FACES = new DualLinkedHashBidiMap<>();
    
    // initialize mapper
    static {
        PRIMITIVE_FACES.put(PrimitiveFace.JING_YA, new Face(Face.JING_YA));
        PRIMITIVE_FACES.put(PrimitiveFace.PIE_ZUI, new Face(Face.PIE_ZUI));
        PRIMITIVE_FACES.put(PrimitiveFace.SE, new Face(Face.SE));
        PRIMITIVE_FACES.put(PrimitiveFace.FA_DAI, new Face(Face.FA_DAI));
        PRIMITIVE_FACES.put(PrimitiveFace.DE_YI, new Face(Face.DE_YI));
        PRIMITIVE_FACES.put(PrimitiveFace.LIU_LEI, new Face(Face.LIU_LEI));
        PRIMITIVE_FACES.put(PrimitiveFace.HAI_XIU, new Face(Face.HAI_XIU));
        PRIMITIVE_FACES.put(PrimitiveFace.BI_ZUI, new Face(Face.BI_ZUI));
        PRIMITIVE_FACES.put(PrimitiveFace.SHUI, new Face(Face.SHUI));
        PRIMITIVE_FACES.put(PrimitiveFace.DA_KU, new Face(Face.DA_KU));
        PRIMITIVE_FACES.put(PrimitiveFace.GAN_GA, new Face(Face.GAN_GA));
        PRIMITIVE_FACES.put(PrimitiveFace.FA_NU, new Face(Face.FA_NU));
        PRIMITIVE_FACES.put(PrimitiveFace.TIAO_PI, new Face(Face.TIAO_PI));
        PRIMITIVE_FACES.put(PrimitiveFace.ZI_YA, new Face(Face.ZI_YA));
        PRIMITIVE_FACES.put(PrimitiveFace.WEI_XIAO, new Face(Face.WEI_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.NAN_GUO, new Face(Face.NAN_GUO));
        PRIMITIVE_FACES.put(PrimitiveFace.KU, new Face(Face.KU));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHUA_KUANG, new Face(Face.ZHUA_KUANG));
        PRIMITIVE_FACES.put(PrimitiveFace.TU, new Face(Face.TU));
        PRIMITIVE_FACES.put(PrimitiveFace.TOU_XIAO, new Face(Face.TOU_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.KE_AI, new Face(Face.KE_AI));
        PRIMITIVE_FACES.put(PrimitiveFace.BAI_YAN, new Face(Face.BAI_YAN));
        PRIMITIVE_FACES.put(PrimitiveFace.AO_MAN, new Face(Face.AO_MAN));
        PRIMITIVE_FACES.put(PrimitiveFace.JI_E, new Face(Face.JI_E));
        PRIMITIVE_FACES.put(PrimitiveFace.KUN, new Face(Face.KUN));
        PRIMITIVE_FACES.put(PrimitiveFace.JING_KONG, new Face(Face.JING_KONG));
        PRIMITIVE_FACES.put(PrimitiveFace.LIU_HAN, new Face(Face.LIU_HAN));
        PRIMITIVE_FACES.put(PrimitiveFace.HAN_XIAO, new Face(Face.HAN_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.YOU_XIAN, new Face(Face.YOU_XIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.FEN_DOU, new Face(Face.FEN_DOU));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHOU_MA, new Face(Face.ZHOU_MA));
        PRIMITIVE_FACES.put(PrimitiveFace.YI_WEN, new Face(Face.YI_WEN));
        PRIMITIVE_FACES.put(PrimitiveFace.XU, new Face(Face.XU));
        PRIMITIVE_FACES.put(PrimitiveFace.YUN, new Face(Face.YUN));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHE_MO, new Face(Face.ZHE_MO));
        PRIMITIVE_FACES.put(PrimitiveFace.SHUAI, new Face(Face.SHUAI));
        PRIMITIVE_FACES.put(PrimitiveFace.KU_LOU, new Face(Face.KU_LOU));
        PRIMITIVE_FACES.put(PrimitiveFace.QIAO_DA, new Face(Face.QIAO_DA));
        PRIMITIVE_FACES.put(PrimitiveFace.ZAI_JIAN, new Face(Face.ZAI_JIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.FA_DOU, new Face(Face.FA_DOU));
        PRIMITIVE_FACES.put(PrimitiveFace.AI_QING, new Face(Face.AI_QING));
        PRIMITIVE_FACES.put(PrimitiveFace.TIAO_TIAO, new Face(Face.TIAO_TIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHU_TOU, new Face(Face.ZHU_TOU));
        PRIMITIVE_FACES.put(PrimitiveFace.YONG_BAO, new Face(Face.YONG_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.DAN_GAO, new Face(Face.DAN_GAO));
        PRIMITIVE_FACES.put(PrimitiveFace.SHAN_DIAN, new Face(Face.SHAN_DIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHA_DAN, new Face(Face.ZHA_DAN));
        PRIMITIVE_FACES.put(PrimitiveFace.DAO, new Face(Face.DAO));
        PRIMITIVE_FACES.put(PrimitiveFace.ZU_QIU, new Face(Face.ZU_QIU));
        PRIMITIVE_FACES.put(PrimitiveFace.BIAN_BIAN, new Face(Face.BIAN_BIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.KA_FEI, new Face(Face.KA_FEI));
        PRIMITIVE_FACES.put(PrimitiveFace.FAN, new Face(Face.FAN));
        PRIMITIVE_FACES.put(PrimitiveFace.MEI_GUI, new Face(Face.MEI_GUI));
        PRIMITIVE_FACES.put(PrimitiveFace.DIAO_XIE, new Face(Face.DIAO_XIE));
        PRIMITIVE_FACES.put(PrimitiveFace.AI_XIN, new Face(Face.AI_XIN));
        PRIMITIVE_FACES.put(PrimitiveFace.XIN_SUI, new Face(Face.XIN_SUI));
        PRIMITIVE_FACES.put(PrimitiveFace.LI_WU, new Face(Face.LI_WU));
        PRIMITIVE_FACES.put(PrimitiveFace.TAI_YANG, new Face(Face.TAI_YANG));
        PRIMITIVE_FACES.put(PrimitiveFace.YUE_LIANG, new Face(Face.YUE_LIANG));
        PRIMITIVE_FACES.put(PrimitiveFace.ZAN, new Face(Face.ZAN));
        PRIMITIVE_FACES.put(PrimitiveFace.CAI, new Face(Face.CAI));
        PRIMITIVE_FACES.put(PrimitiveFace.WO_SHOU, new Face(Face.WO_SHOU));
        PRIMITIVE_FACES.put(PrimitiveFace.SHENG_LI, new Face(Face.SHENG_LI));
        PRIMITIVE_FACES.put(PrimitiveFace.FEI_WEN, new Face(Face.FEI_WEN));
        PRIMITIVE_FACES.put(PrimitiveFace.OU_HUO, new Face(Face.OU_HUO));
        PRIMITIVE_FACES.put(PrimitiveFace.XI_GUA, new Face(Face.XI_GUA));
        PRIMITIVE_FACES.put(PrimitiveFace.LENG_HAN, new Face(Face.LENG_HAN));
        PRIMITIVE_FACES.put(PrimitiveFace.CA_HAN, new Face(Face.CA_HAN));
        PRIMITIVE_FACES.put(PrimitiveFace.KOU_BI, new Face(Face.KOU_BI));
        PRIMITIVE_FACES.put(PrimitiveFace.GU_ZHANG, new Face(Face.GU_ZHANG));
        PRIMITIVE_FACES.put(PrimitiveFace.QIU_DA_LE, new Face(Face.QIU_DA_LE));
        PRIMITIVE_FACES.put(PrimitiveFace.HUAI_XIAO, new Face(Face.HUAI_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.ZUO_HENG_HENG, new Face(Face.ZUO_HENG_HENG));
        PRIMITIVE_FACES.put(PrimitiveFace.YOU_HENG_HENG, new Face(Face.YOU_HENG_HENG));
        PRIMITIVE_FACES.put(PrimitiveFace.HA_QIAN, new Face(Face.HA_QIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.BI_SHI, new Face(Face.BI_SHI));
        PRIMITIVE_FACES.put(PrimitiveFace.WEI_QU, new Face(Face.WEI_QU));
        PRIMITIVE_FACES.put(PrimitiveFace.KUAI_KU_LE, new Face(Face.KUAI_KU_LE));
        PRIMITIVE_FACES.put(PrimitiveFace.YIN_XIAN, new Face(Face.YIN_XIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.QIN_QIN, new Face(Face.QIN_QIN));
        PRIMITIVE_FACES.put(PrimitiveFace.ZUO_QIN_QIN, new Face(Face.ZUO_QIN_QIN));
        PRIMITIVE_FACES.put(PrimitiveFace.XIA, new Face(Face.XIA));
        PRIMITIVE_FACES.put(PrimitiveFace.KE_LIAN, new Face(Face.KE_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.CAI_DAO, new Face(Face.CAI_DAO));
        PRIMITIVE_FACES.put(PrimitiveFace.PI_JIU, new Face(Face.PI_JIU));
        PRIMITIVE_FACES.put(PrimitiveFace.LAN_QIU, new Face(Face.LAN_QIU));
        PRIMITIVE_FACES.put(PrimitiveFace.PING_PANG, new Face(Face.PING_PANG));
        PRIMITIVE_FACES.put(PrimitiveFace.SHI_AI, new Face(Face.SHI_AI));
        PRIMITIVE_FACES.put(PrimitiveFace.PIAO_CHONG, new Face(Face.PIAO_CHONG));
        PRIMITIVE_FACES.put(PrimitiveFace.BAO_QUAN, new Face(Face.BAO_QUAN));
        PRIMITIVE_FACES.put(PrimitiveFace.GOU_YIN, new Face(Face.GOU_YIN));
        PRIMITIVE_FACES.put(PrimitiveFace.QUAN_TOU, new Face(Face.QUAN_TOU));
        PRIMITIVE_FACES.put(PrimitiveFace.CHA_JIN, new Face(Face.CHA_JIN));
        PRIMITIVE_FACES.put(PrimitiveFace.AI_NI, new Face(Face.AI_NI));
        PRIMITIVE_FACES.put(PrimitiveFace.NO, new Face(Face.NO));
        PRIMITIVE_FACES.put(PrimitiveFace.OK, new Face(Face.OK));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHUAN_QUAN, new Face(Face.ZHUAN_QUAN));
        PRIMITIVE_FACES.put(PrimitiveFace.KE_TOU, new Face(Face.KE_TOU));
        PRIMITIVE_FACES.put(PrimitiveFace.HUI_TOU, new Face(Face.HUI_TOU));
        PRIMITIVE_FACES.put(PrimitiveFace.TIAO_SHENG, new Face(Face.TIAO_SHENG));
        PRIMITIVE_FACES.put(PrimitiveFace.HUI_SHOU, new Face(Face.HUI_SHOU));
        PRIMITIVE_FACES.put(PrimitiveFace.JI_DONG, new Face(Face.JI_DONG));
        PRIMITIVE_FACES.put(PrimitiveFace.JIE_WU, new Face(Face.JIE_WU));
        PRIMITIVE_FACES.put(PrimitiveFace.XIAN_WEN, new Face(Face.XIAN_WEN));
        PRIMITIVE_FACES.put(PrimitiveFace.ZUO_TAI_JI, new Face(Face.ZUO_TAI_JI));
        PRIMITIVE_FACES.put(PrimitiveFace.YOU_TAI_JI, new Face(Face.YOU_TAI_JI));
        PRIMITIVE_FACES.put(PrimitiveFace.SHUANG_XI, new Face(Face.SHUANG_XI));
        PRIMITIVE_FACES.put(PrimitiveFace.BIAN_PAO, new Face(Face.BIAN_PAO));
        PRIMITIVE_FACES.put(PrimitiveFace.DENG_LONG, new Face(Face.DENG_LONG));
        PRIMITIVE_FACES.put(PrimitiveFace.K_GE, new Face(Face.K_GE));
        PRIMITIVE_FACES.put(PrimitiveFace.HE_CAI, new Face(Face.HE_CAI));
        PRIMITIVE_FACES.put(PrimitiveFace.QI_DAO, new Face(Face.QI_DAO));
        PRIMITIVE_FACES.put(PrimitiveFace.BAO_JIN, new Face(Face.BAO_JIN));
        PRIMITIVE_FACES.put(PrimitiveFace.BANG_BANG_TANG, new Face(Face.BANG_BANG_TANG));
        PRIMITIVE_FACES.put(PrimitiveFace.HE_NAI, new Face(Face.HE_NAI));
        PRIMITIVE_FACES.put(PrimitiveFace.FEI_JI, new Face(Face.FEI_JI));
        PRIMITIVE_FACES.put(PrimitiveFace.CHAO_PIAO, new Face(Face.CHAO_PIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.YAO, new Face(Face.YAO));
        PRIMITIVE_FACES.put(PrimitiveFace.SHOU_QIANG, new Face(Face.SHOU_QIANG));
        PRIMITIVE_FACES.put(PrimitiveFace.CHA, new Face(Face.CHA));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHA_YAN_JING, new Face(Face.ZHA_YAN_JING));
        PRIMITIVE_FACES.put(PrimitiveFace.LEI_BEN, new Face(Face.LEI_BEN));
        PRIMITIVE_FACES.put(PrimitiveFace.WU_NAI, new Face(Face.WU_NAI));
        PRIMITIVE_FACES.put(PrimitiveFace.MAI_MENG, new Face(Face.MAI_MENG));
        PRIMITIVE_FACES.put(PrimitiveFace.XIAO_JIU_JIE, new Face(Face.XIAO_JIU_JIE));
        PRIMITIVE_FACES.put(PrimitiveFace.PEN_XIE, new Face(Face.PEN_XIE));
        PRIMITIVE_FACES.put(PrimitiveFace.XIE_YAN_XIAO, new Face(Face.XIE_YAN_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.DOGE, new Face(Face.doge));
        PRIMITIVE_FACES.put(PrimitiveFace.JING_XI, new Face(Face.JING_XI));
        PRIMITIVE_FACES.put(PrimitiveFace.SAO_RAO, new Face(Face.SAO_RAO));
        PRIMITIVE_FACES.put(PrimitiveFace.XIAO_KU, new Face(Face.XIAO_KU));
        PRIMITIVE_FACES.put(PrimitiveFace.WO_ZUI_MEI, new Face(Face.WO_ZUI_MEI));
        PRIMITIVE_FACES.put(PrimitiveFace.HE_XIE, new Face(Face.HE_XIE));
        PRIMITIVE_FACES.put(PrimitiveFace.YANG_TUO, new Face(Face.YANG_TUO));
        PRIMITIVE_FACES.put(PrimitiveFace.YOU_LING, new Face(Face.YOU_LING));
        PRIMITIVE_FACES.put(PrimitiveFace.DAN, new Face(Face.DAN));
        PRIMITIVE_FACES.put(PrimitiveFace.JU_HUA, new Face(Face.JU_HUA));
        PRIMITIVE_FACES.put(PrimitiveFace.HONG_BAO, new Face(Face.HONG_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.DA_XIAO, new Face(Face.DA_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.BU_KAI_XIN, new Face(Face.BU_KAI_XIN));
        PRIMITIVE_FACES.put(PrimitiveFace.LENG_MO, new Face(Face.LENG_MO));
        PRIMITIVE_FACES.put(PrimitiveFace.E, new Face(Face.E));
        PRIMITIVE_FACES.put(PrimitiveFace.HAO_BANG, new Face(Face.HAO_BANG));
        PRIMITIVE_FACES.put(PrimitiveFace.BAI_TUO, new Face(Face.BAI_TUO));
        PRIMITIVE_FACES.put(PrimitiveFace.DIAN_ZAN, new Face(Face.DIAN_ZAN));
        PRIMITIVE_FACES.put(PrimitiveFace.WU_LIAO, new Face(Face.WU_LIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.TUO_LIAN, new Face(Face.TUO_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.CHI, new Face(Face.CHI));
        PRIMITIVE_FACES.put(PrimitiveFace.SONG_HUA, new Face(Face.SONG_HUA));
        PRIMITIVE_FACES.put(PrimitiveFace.HAI_PA, new Face(Face.HAI_PA));
        PRIMITIVE_FACES.put(PrimitiveFace.HUA_CHI, new Face(Face.HUA_CHI));
        PRIMITIVE_FACES.put(PrimitiveFace.XIAO_YANG_ER, new Face(Face.XIAO_YANG_ER));
        PRIMITIVE_FACES.put(PrimitiveFace.BIAO_LEI, new Face(Face.BIAO_LEI));
        PRIMITIVE_FACES.put(PrimitiveFace.WO_BU_KAN, new Face(Face.WO_BU_KAN));
        PRIMITIVE_FACES.put(PrimitiveFace.TUO_SAI, new Face(Face.TUO_SAI));
        PRIMITIVE_FACES.put(PrimitiveFace.BO_BO, new Face(Face.BO_BO));
        PRIMITIVE_FACES.put(PrimitiveFace.HU_LIAN, new Face(Face.HU_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.PAI_TOU, new Face(Face.PAI_TOU));
        PRIMITIVE_FACES.put(PrimitiveFace.CHE_YI_CHE, new Face(Face.CHE_YI_CHE));
        PRIMITIVE_FACES.put(PrimitiveFace.TIAN_YI_TIAN, new Face(Face.TIAN_YI_TIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.CENG_YI_CENG, new Face(Face.CENG_YI_CENG));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHUAI_ZHA_TIAN, new Face(Face.ZHUAI_ZHA_TIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.DING_GUA_GUA, new Face(Face.DING_GUA_GUA));
        PRIMITIVE_FACES.put(PrimitiveFace.BAO_BAO, new Face(Face.BAO_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.BAO_JI, new Face(Face.BAO_JI));
        PRIMITIVE_FACES.put(PrimitiveFace.KAI_QIANG, new Face(Face.KAI_QIANG));
        PRIMITIVE_FACES.put(PrimitiveFace.LIAO_YI_LIAO, new Face(Face.LIAO_YI_LIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.PAI_ZHUO, new Face(Face.PAI_ZHUO));
        PRIMITIVE_FACES.put(PrimitiveFace.PAI_SHOU, new Face(Face.PAI_SHOU));
        PRIMITIVE_FACES.put(PrimitiveFace.GONG_XI, new Face(Face.GONG_XI));
        PRIMITIVE_FACES.put(PrimitiveFace.GAN_BEI, new Face(Face.GAN_BEI));
        PRIMITIVE_FACES.put(PrimitiveFace.CHAO_FENG, new Face(Face.CHAO_FENG));
        PRIMITIVE_FACES.put(PrimitiveFace.HENG, new Face(Face.HENG));
        PRIMITIVE_FACES.put(PrimitiveFace.FO_XI, new Face(Face.FO_XI));
        PRIMITIVE_FACES.put(PrimitiveFace.QIA_YI_QIA, new Face(Face.QIA_YI_QIA));
        PRIMITIVE_FACES.put(PrimitiveFace.JING_DAI, new Face(Face.JING_DAI));
        PRIMITIVE_FACES.put(PrimitiveFace.CHAN_DOU, new Face(Face.CHAN_DOU));
        PRIMITIVE_FACES.put(PrimitiveFace.KEN_TOU, new Face(Face.KEN_TOU));
        PRIMITIVE_FACES.put(PrimitiveFace.TOU_KAN, new Face(Face.TOU_KAN));
        PRIMITIVE_FACES.put(PrimitiveFace.SHAN_LIAN, new Face(Face.SHAN_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.YUAN_LIANG, new Face(Face.YUAN_LIANG));
        PRIMITIVE_FACES.put(PrimitiveFace.PEN_LIAN, new Face(Face.PEN_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.SHENG_RI_KUAI_LE, new Face(Face.SHENG_RI_KUAI_LE));
        PRIMITIVE_FACES.put(PrimitiveFace.TOU_ZHUANG_JI, new Face(Face.TOU_ZHUANG_JI));
        PRIMITIVE_FACES.put(PrimitiveFace.SHUAI_TOU, new Face(Face.SHUAI_TOU));
        PRIMITIVE_FACES.put(PrimitiveFace.RENG_GOU, new Face(Face.RENG_GOU));
        PRIMITIVE_FACES.put(PrimitiveFace.JIA_YOU_BI_SHENG, new Face(Face.JIA_YOU_BI_SHENG));
        PRIMITIVE_FACES.put(PrimitiveFace.JIA_YOU_BAO_BAO, new Face(Face.JIA_YOU_BAO_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.KOU_ZHAO_HU_TI, new Face(Face.KOU_ZHAO_HU_TI));
        PRIMITIVE_FACES.put(PrimitiveFace.BAN_ZHUAN_ZHONG, new Face(Face.BAN_ZHUAN_ZHONG));
        PRIMITIVE_FACES.put(PrimitiveFace.MANG_DAO_FEI_QI, new Face(Face.MANG_DAO_FEI_QI));
        PRIMITIVE_FACES.put(PrimitiveFace.NAO_KUO_TENG, new Face(Face.NAO_KUO_TENG));
        PRIMITIVE_FACES.put(PrimitiveFace.CANG_SANG, new Face(Face.CANG_SANG));
        PRIMITIVE_FACES.put(PrimitiveFace.WU_LIAN, new Face(Face.WU_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.LA_YAN_JING, new Face(Face.LA_YAN_JING));
        PRIMITIVE_FACES.put(PrimitiveFace.O_YO, new Face(Face.O_YO));
        PRIMITIVE_FACES.put(PrimitiveFace.TOU_TU, new Face(Face.TOU_TU));
        PRIMITIVE_FACES.put(PrimitiveFace.WEN_HAO_LIAN, new Face(Face.WEN_HAO_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.AN_ZHONG_GUAN_CHA, new Face(Face.AN_ZHONG_GUAN_CHA));
        PRIMITIVE_FACES.put(PrimitiveFace.EMM, new Face(Face.emm));
        PRIMITIVE_FACES.put(PrimitiveFace.CHI_GUA, new Face(Face.CHI_GUA));
        PRIMITIVE_FACES.put(PrimitiveFace.HE_HE_DA, new Face(Face.HE_HE_DA));
        PRIMITIVE_FACES.put(PrimitiveFace.WO_SUAN_LE, new Face(Face.WO_SUAN_LE));
        PRIMITIVE_FACES.put(PrimitiveFace.TAI_NAN_LE, new Face(Face.TAI_NAN_LE));
        PRIMITIVE_FACES.put(PrimitiveFace.LA_JIAO_JIANG, new Face(Face.LA_JIAO_JIANG));
        PRIMITIVE_FACES.put(PrimitiveFace.WANG_WANG, new Face(Face.WANG_WANG));
        PRIMITIVE_FACES.put(PrimitiveFace.HAN, new Face(Face.HAN));
        PRIMITIVE_FACES.put(PrimitiveFace.DA_LIAN, new Face(Face.DA_LIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.JI_ZHANG, new Face(Face.JI_ZHANG));
        PRIMITIVE_FACES.put(PrimitiveFace.WU_YAN_XIAO, new Face(Face.WU_YAN_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.JING_LI, new Face(Face.JING_LI));
        PRIMITIVE_FACES.put(PrimitiveFace.KUANG_XIAO, new Face(Face.KUANG_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.MIAN_WU_BIAO_QING, new Face(Face.MIAN_WU_BIAO_QING));
        PRIMITIVE_FACES.put(PrimitiveFace.MO_YU, new Face(Face.MO_YU));
        PRIMITIVE_FACES.put(PrimitiveFace.MO_GUI_XIAO, new Face(Face.MO_GUI_XIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.O, new Face(Face.O));
        PRIMITIVE_FACES.put(PrimitiveFace.QING, new Face(Face.QING));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHENG_YAN, new Face(Face.ZHENG_YAN));
        PRIMITIVE_FACES.put(PrimitiveFace.QIAO_KAI_XIN, new Face(Face.QIAO_KAI_XIN));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHEN_JING, new Face(Face.ZHEN_JING));
        PRIMITIVE_FACES.put(PrimitiveFace.RANG_WO_KANG_KANG, new Face(Face.RANG_WO_KANG_KANG));
        PRIMITIVE_FACES.put(PrimitiveFace.MO_JIN_LI, new Face(Face.MO_JIN_LI));
        PRIMITIVE_FACES.put(PrimitiveFace.QI_DAI, new Face(Face.QI_DAI));
        PRIMITIVE_FACES.put(PrimitiveFace.NA_DAO_HONG_BAO, new Face(Face.NA_DAO_HONG_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.ZHEN_HAO, new Face(Face.ZHEN_HAO));
        PRIMITIVE_FACES.put(PrimitiveFace.BAI_XIE, new Face(Face.BAI_XIE));
        PRIMITIVE_FACES.put(PrimitiveFace.YUAN_BAO, new Face(Face.YUAN_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.NIU_A, new Face(Face.NIU_A));
        PRIMITIVE_FACES.put(PrimitiveFace.PANG_SAN_JIN, new Face(Face.PANG_SAN_JIN));
        PRIMITIVE_FACES.put(PrimitiveFace.HAO_SHAN, new Face(Face.HAO_SHAN));
        PRIMITIVE_FACES.put(PrimitiveFace.ZUO_BAI_NIAN, new Face(Face.ZUO_BAI_NIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.YOU_BAI_NIAN, new Face(Face.YOU_BAI_NIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.HONG_BAO_BAO, new Face(Face.HONG_BAO_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.YOU_QIN_QIN, new Face(Face.YOU_QIN_QIN));
        PRIMITIVE_FACES.put(PrimitiveFace.NIU_QI_CHONG_TIAN, new Face(Face.NIU_QI_CHONG_TIAN));
        PRIMITIVE_FACES.put(PrimitiveFace.MIAO_MIAO, new Face(Face.MIAO_MIAO));
        PRIMITIVE_FACES.put(PrimitiveFace.QIU_HONG_BAO, new Face(Face.QIU_HONG_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.XIE_HONG_BAO, new Face(Face.XIE_HONG_BAO));
        PRIMITIVE_FACES.put(PrimitiveFace.XIN_NIAN_YAN_HUA, new Face(Face.XIN_NIAN_YAN_HUA));
        PRIMITIVE_FACES.put(PrimitiveFace.DA_CALL, new Face(Face.DA_CALL));
        PRIMITIVE_FACES.put(PrimitiveFace.BIAN_XING, new Face(Face.BIAN_XING));
        PRIMITIVE_FACES.put(PrimitiveFace.KE_DAO_LE, new Face(Face.KE_DAO_LE));
        PRIMITIVE_FACES.put(PrimitiveFace.ZI_XI_FEN_XI, new Face(Face.ZI_XI_FEN_XI));
        PRIMITIVE_FACES.put(PrimitiveFace.JIA_YOU, new Face(Face.JIA_YOU));
        PRIMITIVE_FACES.put(PrimitiveFace.WO_MEI_SHI, new Face(Face.WO_MEI_SHI));
        PRIMITIVE_FACES.put(PrimitiveFace.CAI_GOU, new Face(Face.CAI_GOU));
        PRIMITIVE_FACES.put(PrimitiveFace.CHONG_BAI, new Face(Face.CHONG_BAI));
        PRIMITIVE_FACES.put(PrimitiveFace.BI_XIN, new Face(Face.BI_XIN));
        PRIMITIVE_FACES.put(PrimitiveFace.QING_ZHU, new Face(Face.QING_ZHU));
        PRIMITIVE_FACES.put(PrimitiveFace.LAO_SE_PI, new Face(Face.LAO_SE_PI));
        PRIMITIVE_FACES.put(PrimitiveFace.JU_JUE, new Face(Face.JU_JUE));
        PRIMITIVE_FACES.put(PrimitiveFace.XIAN_QI, new Face(Face.XIAN_QI));
        PRIMITIVE_FACES.put(PrimitiveFace.CHI_TANG, new Face(Face.CHI_TANG));
    }
    
    /**
     * 将小明表情转化为 Qq 表情 Id
     *
     * @param primitiveFace 小明表情
     * @return Qq 表情 Id
     */
    @SuppressWarnings("all")
    public static int toQqFaceId(PrimitiveFace primitiveFace) {
        Preconditions.objectNonNull(primitiveFace, "primitiveFace");
        return primitiveFace.getCode();
    }
    
    /**
     * 将小明表情转化为 Qq 表情
     *
     * @param primitiveFace 小明表情
     * @return Qq 表情
     */
    public static Face toQq(PrimitiveFace primitiveFace) {
        return new Face(toQqFaceId(primitiveFace));
    }
    
    /**
     * 将 Qq 表情转化为小明表情
     *
     * @param face Qq 表情
     * @return 小明表情
     */
    @SuppressWarnings("all")
    public static PrimitiveFace toXiaoMing(Face face) {
        Preconditions.objectNonNull(face, "face");
    
        final PrimitiveFace primitiveFace = PRIMITIVE_FACES.getKey(face);
        Preconditions.elementNonNull(primitiveFace);
        return primitiveFace;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message source
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * 将消息源转换为 qq 消息源
     *
     * @param messageSource 消息源
     * @param properties    相关属性
     * @return qq 消息源
     * @throws NullPointerException messageSource 或 properties 为 null
     */
    public static MessageSource toQq(cn.codethink.xiaoming.message.metadata.MessageSource messageSource, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(messageSource, "message source");
        Preconditions.objectNonNull(properties, "properties");
    
        // if it's qq reference, use it
        if (messageSource instanceof AbstractQqOnlineMessageSource) {
            return ((AbstractQqOnlineMessageSource) messageSource).getQqMessageSource();
        }
        if (messageSource instanceof QqOfflineMessageSource) {
            return ((QqOfflineMessageSource) messageSource).getQqMessageSource();
        }
        
        // build an offline message source
        final MessageSourceBuilder messageSourceBuilder = new MessageSourceBuilder()
            .messages(toQq(messageSource.getMessage().asCompoundMessage(), properties))
            .time((int) TimeUnit.MILLISECONDS.toSeconds(messageSource.getTimestamp()));
        
        messageSourceBuilder.setFromId(messageSource.getSourceCode().asLong());
        messageSourceBuilder.setTargetId(messageSource.getTargetCode().asLong());
    
        return messageSourceBuilder
            .build(messageSource.getBotCode().asLong(), toQq(messageSource.getMessageSourceType()));
    }
    
    /**
     * 将 qq 消息源转化为小明消息源。
     *
     * @param messageSource qq 消息源
     * @param properties    相关属性
     * @return 小明消息源
     * @throws NullPointerException messageSource 或 properties 为 null
     */
    public static cn.codethink.xiaoming.message.metadata.MessageSource toXiaoMing(MessageSource messageSource, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(messageSource, "message source");
        Preconditions.objectNonNull(properties, "properties");
    
        if (messageSource instanceof OfflineMessageSource) {
            final OfflineMessageSource offlineMessageSource = (OfflineMessageSource) messageSource;
            return new QqOfflineMessageSource(offlineMessageSource, properties);
        }
        if (messageSource instanceof OnlineMessageSource) {
            final QqBot bot = (QqBot) Property.BOT.getOrFail(properties);
    
            final OnlineMessageSource onlineMessageSource = (OnlineMessageSource) messageSource;
            if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromFriend) {
                final OnlineMessageSource.Incoming.FromFriend qqMessageSource = (OnlineMessageSource.Incoming.FromFriend) onlineMessageSource;
    
                final QqFriend friend = bot.getFriendOrFail(Code.ofLong(qqMessageSource.getFromId()));
                return new QqFromFriendMessageSource(qqMessageSource, friend, properties);
            }
    
            if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromGroup) {
                final OnlineMessageSource.Incoming.FromGroup qqMessageSource = (OnlineMessageSource.Incoming.FromGroup) onlineMessageSource;
        
                final QqGroup qqGroup = bot
                    .getGroupOrFail(Code.ofLong(qqMessageSource.getGroup().getId()));
        
                final Member sender = qqMessageSource.getSender();
                final GroupSender groupSender;
                if (sender instanceof NormalMember) {
                    groupSender = qqGroup.getMemberOrFail(Code.ofLong(sender.getId()));
                } else if (sender instanceof AnonymousMember) {
                    final AnonymousMember anonymousMember = (AnonymousMember) sender;
                    groupSender = qqGroup.getAnonymous().getAvailable(anonymousMember);
                } else {
                    throw new IllegalArgumentException("unknown group member: " + sender);
                }
        
                return new QqFromGroupMessageSource(qqMessageSource, groupSender, properties);
            }
    
            if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromStranger) {
                final OnlineMessageSource.Incoming.FromStranger qqMessageSource = (OnlineMessageSource.Incoming.FromStranger) onlineMessageSource;
        
                final QqStranger stranger = bot.getStrangerOrFail(Code.ofLong(qqMessageSource.getFromId()));
                return new QqFromStrangerMessageSource(qqMessageSource, stranger, properties);
            }
    
            if (onlineMessageSource instanceof OnlineMessageSource.Incoming.FromTemp) {
                final OnlineMessageSource.Incoming.FromTemp qqMessageSource = (OnlineMessageSource.Incoming.FromTemp) onlineMessageSource;
        
                final QqMember member = bot
                    .getGroupOrFail(Code.ofLong(qqMessageSource.getGroup().getId()))
                    .getMemberOrFail(Code.ofLong(qqMessageSource.getSender().getId()));
        
                return new QqFromGroupMemberMessageSource(qqMessageSource, member, properties);
            }
    
            if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToFriend) {
                final OnlineMessageSource.Outgoing.ToFriend qqMessageSource = (OnlineMessageSource.Outgoing.ToFriend) onlineMessageSource;
        
                final QqFriend friend = bot.getFriendOrFail(Code.ofLong(qqMessageSource.getTargetId()));
                return new QqToFriendMessageSource(qqMessageSource, friend, properties);
            }
    
            if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToGroup) {
                final OnlineMessageSource.Outgoing.ToGroup qqMessageSource = (OnlineMessageSource.Outgoing.ToGroup) onlineMessageSource;
        
                final QqGroup group = bot.getGroupOrFail(Code.ofLong(qqMessageSource.getTargetId()));
                return new QqToGroupMessageSource(qqMessageSource, group, properties);
            }
    
            if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToTemp) {
                final OnlineMessageSource.Outgoing.ToTemp qqMessageSource = (OnlineMessageSource.Outgoing.ToTemp) onlineMessageSource;
        
                final QqMember member = bot.getGroupOrFail(Code.ofLong(qqMessageSource.getGroup().getId()))
                    .getMemberOrFail(Code.ofLong(qqMessageSource.getTargetId()));
        
                return new QqToGroupMemberMessageSource(qqMessageSource, member, properties);
            }
    
            if (onlineMessageSource instanceof OnlineMessageSource.Outgoing.ToStranger) {
                final OnlineMessageSource.Outgoing.ToStranger qqMessageSource = (OnlineMessageSource.Outgoing.ToStranger) onlineMessageSource;
        
                final QqStranger stranger = bot.getStrangerOrFail(Code.ofLong(qqMessageSource.getTargetId()));
                return new QqToStrangerMessageSource(qqMessageSource, stranger, properties);
            }
    
            throw new NoSuchElementException("can not convert the online message source: " + onlineMessageSource);
        }
    
        throw new NoSuchElementException("can not convert the message source: " + messageSource);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // protocol
    ///////////////////////////////////////////////////////////////////////////
    
    private static final BidiMap<Protocol, BotConfiguration.MiraiProtocol> PROTOCOLS = new DualLinkedHashBidiMap<>();
    
    // initialize mapper
    static {
        PROTOCOLS.put(Protocol.ANDROID_PAD, BotConfiguration.MiraiProtocol.ANDROID_PAD);
        PROTOCOLS.put(Protocol.ANDROID_PHONE, BotConfiguration.MiraiProtocol.ANDROID_PHONE);
        PROTOCOLS.put(Protocol.ANDROID_WATCH, BotConfiguration.MiraiProtocol.ANDROID_WATCH);
    }
    
    /**
     * 将小明协议转化为 qq 协议
     *
     * @param protocol 小明协议
     * @return qq 协议
     * @throws NullPointerException   protocol 为 null
     * @throws NoSuchElementException 找不到对应的 qq 协议
     */
    public static BotConfiguration.MiraiProtocol toQq(Protocol protocol) {
        return map(protocol, PROTOCOLS);
    }
    
    /**
     * 将 qq 协议转化为小明协议
     *
     * @param protocol qq 协议
     * @return 小明协议
     * @throws NullPointerException   protocol 为 null
     * @throws NoSuchElementException 找不到对应的小明协议
     */
    public static Protocol toXiaoMing(BotConfiguration.MiraiProtocol protocol) {
        return map(protocol, PROTOCOLS);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // sex
    ///////////////////////////////////////////////////////////////////////////
    
    private static final BidiMap<Sex, UserProfile.Sex> SEXES = new DualLinkedHashBidiMap<>();
    
    /**
     * 将 qq 性别转化为小明性别
     *
     * @param sex qq 性别
     * @return 小明性别
     * @throws NullPointerException     sex 为 null
     * @throws IllegalArgumentException 找不到对应的小明性别
     */
    public static Sex toXiaoMing(UserProfile.Sex sex) {
        return map(sex, SEXES);
    }
    
    /**
     * 将小明性别转化为 qq 性别
     *
     * @param sex 小明性别
     * @return Qq 性别
     * @throws NullPointerException     sex 为 null
     * @throws IllegalArgumentException 找不到对应的 qq 性别
     */
    public static UserProfile.Sex toQq(Sex sex) {
        return map(sex, SEXES);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // role
    ///////////////////////////////////////////////////////////////////////////
    
    private static final BidiMap<Role, MemberPermission> ROLES = new DualLinkedHashBidiMap<>();
    
    // initialize mapper
    static {
        ROLES.put(Role.OWNER, MemberPermission.OWNER);
        ROLES.put(Role.MEMBER, MemberPermission.MEMBER);
        ROLES.put(Role.ADMIN, MemberPermission.ADMINISTRATOR);
    }
    
    /**
     * 将 Qq 群员权限转化为小明角色
     *
     * @param permission Qq 群员权限
     * @return 小明角色
     */
    public static Role toXiaoMing(MemberPermission permission) {
        return map(permission, ROLES);
    }
    
    /**
     * 将小明角色转化为 Qq 群员权限
     *
     * @param role 小明角色
     * @return Qq 群员权限
     */
    public static MemberPermission toQq(Role role) {
        return map(role, ROLES);
    }
}