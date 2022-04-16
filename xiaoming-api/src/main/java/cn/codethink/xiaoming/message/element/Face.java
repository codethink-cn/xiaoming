package cn.codethink.xiaoming.message.element;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.SingletonCompoundMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 原生表情
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public enum Face
    implements BasicMessage {
    
    /**
     * 惊讶
     */
    JING_YA("惊讶", 0),
    
    /**
     * 撇嘴
     */
    PIE_ZUI("撇嘴", 1),
    
    /**
     * 色
     */
    SE("色", 2),
    
    /**
     * 发呆
     */
    FA_DAI("发呆", 3),
    
    /**
     * 得意
     */
    DE_YI("得意", 4),
    
    /**
     * 流泪
     */
    LIU_LEI("流泪", 5),
    
    /**
     * 害羞
     */
    HAI_XIU("害羞", 6),
    
    /**
     * 闭嘴
     */
    BI_ZUI("闭嘴", 7),
    
    /**
     * 睡
     */
    SHUI("睡", 8),
    
    /**
     * 大哭
     */
    DA_KU("大哭", 9),
    
    /**
     * 尴尬
     */
    GAN_GA("尴尬", 10),
    
    /**
     * 发怒
     */
    FA_NU("发怒", 11),
    
    /**
     * 调皮
     */
    TIAO_PI("调皮", 12),
    
    /**
     * 呲牙
     */
    ZI_YA("呲牙", 13),
    
    /**
     * 微笑
     */
    WEI_XIAO("微笑", 14),
    
    /**
     * 难过
     */
    NAN_GUO("难过", 15),
    
    /**
     * 酷
     */
    KU("酷", 16),
    
    /**
     * 抓狂
     */
    ZHUA_KUANG("抓狂", 18),
    
    /**
     * 吐
     */
    TU("吐", 19),
    
    /**
     * 偷笑
     */
    TOU_XIAO("偷笑", 20),
    
    /**
     * 可爱
     */
    KE_AI("可爱", 21),
    
    /**
     * 白眼
     */
    BAI_YAN("白眼", 22),
    
    /**
     * 傲慢
     */
    AO_MAN("傲慢", 23),
    
    /**
     * 饥饿
     */
    JI_E("饥饿", 24),
    
    /**
     * 困
     */
    KUN("困", 25),
    
    /**
     * 惊恐
     */
    JING_KONG("惊恐", 26),
    
    /**
     * 流汗
     */
    LIU_HAN("流汗", 27),
    
    /**
     * 憨笑
     */
    HAN_XIAO("憨笑", 28),
    
    /**
     * 悠闲
     */
    YOU_XIAN("悠闲", 29),
    
    /**
     * 奋斗
     */
    FEN_DOU("奋斗", 30),
    
    /**
     * 咒骂
     */
    ZHOU_MA("咒骂", 31),
    
    /**
     * 疑问
     */
    YI_WEN("疑问", 32),
    
    /**
     * 嘘
     */
    XU("嘘", 33),
    
    /**
     * 晕
     */
    YUN("晕", 34),
    
    /**
     * 折磨
     */
    ZHE_MO("折磨", 35),
    
    /**
     * 衰
     */
    SHUAI("衰", 36),
    
    /**
     * 骷髅
     */
    KU_LOU("骷髅", 37),
    
    /**
     * 敲打
     */
    QIAO_DA("敲打", 38),
    
    /**
     * 再见
     */
    ZAI_JIAN("再见", 39),
    
    /**
     * 发抖
     */
    FA_DOU("发抖", 41),
    
    /**
     * 爱情
     */
    AI_QING("爱情", 42),
    
    /**
     * 跳跳
     */
    TIAO_TIAO("跳跳", 43),
    
    /**
     * 猪头
     */
    ZHU_TOU("猪头", 46),
    
    /**
     * 拥抱
     */
    YONG_BAO("拥抱", 49),
    
    /**
     * 蛋糕
     */
    DAN_GAO("蛋糕", 53),
    
    /**
     * 闪电
     */
    SHAN_DIAN("闪电", 54),
    
    /**
     * 炸弹
     */
    ZHA_DAN("炸弹", 55),
    
    /**
     * 刀
     */
    DAO("刀", 56),
    
    /**
     * 足球
     */
    ZU_QIU("足球", 57),
    
    /**
     * 便便
     */
    BIAN_BIAN("便便", 59),
    
    /**
     * 咖啡
     */
    KA_FEI("咖啡", 60),
    
    /**
     * 饭
     */
    FAN("饭", 61),
    
    /**
     * 玫瑰
     */
    MEI_GUI("玫瑰", 63),
    
    /**
     * 凋谢
     */
    DIAO_XIE("凋谢", 64),
    
    /**
     * 爱心
     */
    AI_XIN("爱心", 66),
    
    /**
     * 心碎
     */
    XIN_SUI("心碎", 67),
    
    /**
     * 礼物
     */
    LI_WU("礼物", 69),
    
    /**
     * 太阳
     */
    TAI_YANG("太阳", 74),
    
    /**
     * 月亮
     */
    YUE_LIANG("月亮", 75),
    
    /**
     * 赞
     */
    ZAN("赞", 76),
    
    /**
     * 踩
     */
    CAI("踩", 77),
    
    /**
     * 握手
     */
    WO_SHOU("握手", 78),
    
    /**
     * 胜利
     */
    SHENG_LI("胜利", 79),
    
    /**
     * 飞吻
     */
    FEI_WEN("飞吻", 85),
    
    /**
     * 怄火
     */
    OU_HUO("怄火", 86),
    
    /**
     * 西瓜
     */
    XI_GUA("西瓜", 89),
    
    /**
     * 冷汗
     */
    LENG_HAN("冷汗", 96),
    
    /**
     * 擦汗
     */
    CA_HAN("擦汗", 97),
    
    /**
     * 抠鼻
     */
    KOU_BI("抠鼻", 98),
    
    /**
     * 鼓掌
     */
    GU_ZHANG("鼓掌", 99),
    
    /**
     * 糗大了
     */
    QIU_DA_LE("糗大了", 100),
    
    /**
     * 坏笑
     */
    HUAI_XIAO("坏笑", 101),
    
    /**
     * 左哼哼
     */
    ZUO_HENG_HENG("左哼哼", 102),
    
    /**
     * 右哼哼
     */
    YOU_HENG_HENG("右哼哼", 103),
    
    /**
     * 哈欠
     */
    HA_QIAN("哈欠", 104),
    
    /**
     * 鄙视
     */
    BI_SHI("鄙视", 105),
    
    /**
     * 委屈
     */
    WEI_QU("委屈", 106),
    
    /**
     * 快哭了
     */
    KUAI_KU_LE("快哭了", 107),
    
    /**
     * 阴险
     */
    YIN_XIAN("阴险", 108),
    
    /**
     * 亲亲
     */
    QIN_QIN("亲亲", 109),
    
    /**
     * 左亲亲
     */
    ZUO_QIN_QIN("左亲亲", 109),
    
    /**
     * 吓
     */
    XIA("吓", 110),
    
    /**
     * 可怜
     */
    KE_LIAN("可怜", 111),
    
    /**
     * 菜刀
     */
    CAI_DAO("菜刀", 112),
    
    /**
     * 啤酒
     */
    PI_JIU("啤酒", 113),
    
    /**
     * 篮球
     */
    LAN_QIU("篮球", 114),
    
    /**
     * 乒乓
     */
    PING_PANG("乒乓", 115),
    
    /**
     * 示爱
     */
    SHI_AI("示爱", 116),
    
    /**
     * 瓢虫
     */
    PIAO_CHONG("瓢虫", 117),
    
    /**
     * 抱拳
     */
    BAO_QUAN("抱拳", 118),
    
    /**
     * 勾引
     */
    GOU_YIN("勾引", 119),
    
    /**
     * 拳头
     */
    QUAN_TOU("拳头", 120),
    
    /**
     * 差劲
     */
    CHA_JIN("差劲", 121),
    
    /**
     * 爱你
     */
    AI_NI("爱你", 122),
    
    /**
     * 不
     */
    NO("不", 123),
    
    /**
     * 好
     */
    OK("好", 124),
    
    /**
     * 转圈
     */
    ZHUAN_QUAN("转圈", 125),
    
    /**
     * 磕头
     */
    KE_TOU("磕头", 126),
    
    /**
     * 回头
     */
    HUI_TOU("回头", 127),
    
    /**
     * 跳绳
     */
    TIAO_SHENG("跳绳", 128),
    
    /**
     * 挥手
     */
    HUI_SHOU("挥手", 129),
    
    /**
     * 激动
     */
    JI_DONG("激动", 130),
    
    /**
     * 街舞
     */
    JIE_WU("街舞", 131),
    
    /**
     * 献吻
     */
    XIAN_WEN("献吻", 132),
    
    /**
     * 左太极
     */
    ZUO_TAI_JI("左太极", 133),
    
    /**
     * 右太极
     */
    YOU_TAI_JI("右太极", 134),
    
    /**
     * 双喜
     */
    SHUANG_XI("双喜", 136),
    
    /**
     * 鞭炮
     */
    BIAN_PAO("鞭炮", 137),
    
    /**
     * 灯笼
     */
    DENG_LONG("灯笼", 138),
    
    /**
     * K歌
     */
    K_GE("K歌", 140),
    
    /**
     * 喝彩
     */
    HE_CAI("喝彩", 144),
    
    /**
     * 祈祷
     */
    QI_DAO("祈祷", 145),
    
    /**
     * 爆筋
     */
    BAO_JIN("爆筋", 146),
    
    /**
     * 棒棒糖
     */
    BANG_BANG_TANG("棒棒糖", 147),
    
    /**
     * 喝奶
     */
    HE_NAI("喝奶", 148),
    
    /**
     * 飞机
     */
    FEI_JI("飞机", 151),
    
    /**
     * 钞票
     */
    CHAO_PIAO("钞票", 158),
    
    /**
     * 药
     */
    YAO("药", 168),
    
    /**
     * 手枪
     */
    SHOU_QIANG("手枪", 169),
    
    /**
     * 茶
     */
    CHA("茶", 171),
    
    /**
     * 眨眼睛
     */
    ZHA_YAN_JING("眨眼睛", 172),
    
    /**
     * 泪奔
     */
    LEI_BEN("泪奔", 173),
    
    /**
     * 无奈
     */
    WU_NAI("无奈", 174),
    
    /**
     * 卖萌
     */
    MAI_MENG("卖萌", 175),
    
    /**
     * 小纠结
     */
    XIAO_JIU_JIE("小纠结", 176),
    
    /**
     * 喷血
     */
    PEN_XIE("喷血", 177),
    
    /**
     * 斜眼笑
     */
    XIE_YAN_XIAO("斜眼笑", 178),
    
    /**
     * doge
     */
    doge("doge", 179),
    
    /**
     * 惊喜
     */
    JING_XI("惊喜", 180),
    
    /**
     * 骚扰
     */
    SAO_RAO("骚扰", 181),
    
    /**
     * 笑哭
     */
    XIAO_KU("笑哭", 182),
    
    /**
     * 我最美
     */
    WO_ZUI_MEI("我最美", 183),
    
    /**
     * 河蟹
     */
    HE_XIE("河蟹", 184),
    
    /**
     * 羊驼
     */
    YANG_TUO("羊驼", 185),
    
    /**
     * 幽灵
     */
    YOU_LING("幽灵", 187),
    
    /**
     * 蛋
     */
    DAN("蛋", 188),
    
    /**
     * 菊花
     */
    JU_HUA("菊花", 190),
    
    /**
     * 红包
     */
    HONG_BAO("红包", 192),
    
    /**
     * 大笑
     */
    DA_XIAO("大笑", 193),
    
    /**
     * 不开心
     */
    BU_KAI_XIN("不开心", 194),
    
    /**
     * 冷漠
     */
    LENG_MO("冷漠", 197),
    
    /**
     * 呃
     */
    E("呃", 198),
    
    /**
     * 好棒
     */
    HAO_BANG("好棒", 199),
    
    /**
     * 拜托
     */
    BAI_TUO("拜托", 200),
    
    /**
     * 点赞
     */
    DIAN_ZAN("点赞", 201),
    
    /**
     * 无聊
     */
    WU_LIAO("无聊", 202),
    
    /**
     * 托脸
     */
    TUO_LIAN("托脸", 203),
    
    /**
     * 吃
     */
    CHI("吃", 204),
    
    /**
     * 送花
     */
    SONG_HUA("送花", 205),
    
    /**
     * 害怕
     */
    HAI_PA("害怕", 206),
    
    /**
     * 花痴
     */
    HUA_CHI("花痴", 207),
    
    /**
     * 小样儿
     */
    XIAO_YANG_ER("小样儿", 208),
    
    /**
     * 飙泪
     */
    BIAO_LEI("飙泪", 210),
    
    /**
     * 我不看
     */
    WO_BU_KAN("我不看", 211),
    
    /**
     * 托腮
     */
    TUO_SAI("托腮", 212),
    
    /**
     * 啵啵
     */
    BO_BO("啵啵", 214),
    
    /**
     * 糊脸
     */
    HU_LIAN("糊脸", 215),
    
    /**
     * 拍头
     */
    PAI_TOU("拍头", 216),
    
    /**
     * 扯一扯
     */
    CHE_YI_CHE("扯一扯", 217),
    
    /**
     * 舔一舔
     */
    TIAN_YI_TIAN("舔一舔", 218),
    
    /**
     * 蹭一蹭
     */
    CENG_YI_CENG("蹭一蹭", 219),
    
    /**
     * 拽炸天
     */
    ZHUAI_ZHA_TIAN("拽炸天", 220),
    
    /**
     * 顶呱呱
     */
    DING_GUA_GUA("顶呱呱", 221),
    
    /**
     * 抱抱
     */
    BAO_BAO("抱抱", 222),
    
    /**
     * 暴击
     */
    BAO_JI("暴击", 223),
    
    /**
     * 开枪
     */
    KAI_QIANG("开枪", 224),
    
    /**
     * 撩一撩
     */
    LIAO_YI_LIAO("撩一撩", 225),
    
    /**
     * 拍桌
     */
    PAI_ZHUO("拍桌", 226),
    
    /**
     * 拍手
     */
    PAI_SHOU("拍手", 227),
    
    /**
     * 恭喜
     */
    GONG_XI("恭喜", 228),
    
    /**
     * 干杯
     */
    GAN_BEI("干杯", 229),
    
    /**
     * 嘲讽
     */
    CHAO_FENG("嘲讽", 230),
    
    /**
     * 哼
     */
    HENG("哼", 231),
    
    /**
     * 佛系
     */
    FO_XI("佛系", 232),
    
    /**
     * 掐一掐
     */
    QIA_YI_QIA("掐一掐", 233),
    
    /**
     * 惊呆
     */
    JING_DAI("惊呆", 234),
    
    /**
     * 颤抖
     */
    CHAN_DOU("颤抖", 235),
    
    /**
     * 啃头
     */
    KEN_TOU("啃头", 236),
    
    /**
     * 偷看
     */
    TOU_KAN("偷看", 237),
    
    /**
     * 扇脸
     */
    SHAN_LIAN("扇脸", 238),
    
    /**
     * 原谅
     */
    YUAN_LIANG("原谅", 239),
    
    /**
     * 喷脸
     */
    PEN_LIAN("喷脸", 240),
    
    /**
     * 生日快乐
     */
    SHENG_RI_KUAI_LE("生日快乐", 241),
    
    /**
     * 头撞击
     */
    TOU_ZHUANG_JI("头撞击", 242),
    
    /**
     * 甩头
     */
    SHUAI_TOU("甩头", 243),
    
    /**
     * 扔狗
     */
    RENG_GOU("扔狗", 244),
    
    /**
     * 加油必胜
     */
    JIA_YOU_BI_SHENG("加油必胜", 245),
    
    /**
     * 加油抱抱
     */
    JIA_YOU_BAO_BAO("加油抱抱", 246),
    
    /**
     * 口罩护体
     */
    KOU_ZHAO_HU_TI("口罩护体", 247),
    
    /**
     * 搬砖中
     */
    BAN_ZHUAN_ZHONG("搬砖中", 260),
    
    /**
     * 忙到飞起
     */
    MANG_DAO_FEI_QI("忙到飞起", 261),
    
    /**
     * 脑阔疼
     */
    NAO_KUO_TENG("脑阔疼", 262),
    
    /**
     * 沧桑
     */
    CANG_SANG("沧桑", 263),
    
    /**
     * 捂脸
     */
    WU_LIAN("捂脸", 264),
    
    /**
     * 辣眼睛
     */
    LA_YAN_JING("辣眼睛", 265),
    
    /**
     * 哦哟
     */
    O_YO("哦哟", 266),
    
    /**
     * 头秃
     */
    TOU_TU("头秃", 267),
    
    /**
     * 问号脸
     */
    WEN_HAO_LIAN("问号脸", 268),
    
    /**
     * 暗中观察
     */
    AN_ZHONG_GUAN_CHA("暗中观察", 269),
    
    /**
     * emm
     */
    emm("emm", 270),
    
    /**
     * 吃瓜
     */
    CHI_GUA("吃瓜", 271),
    
    /**
     * 呵呵哒
     */
    HE_HE_DA("呵呵哒", 272),
    
    /**
     * 我酸了
     */
    WO_SUAN_LE("我酸了", 273),
    
    /**
     * 太南了
     */
    TAI_NAN_LE("太南了", 274),
    
    /**
     * 辣椒酱
     */
    LA_JIAO_JIANG("辣椒酱", 276),
    
    /**
     * 汪汪
     */
    WANG_WANG("汪汪", 277),
    
    /**
     * 汗
     */
    HAN("汗", 278),
    
    /**
     * 打脸
     */
    DA_LIAN("打脸", 279),
    
    /**
     * 击掌
     */
    JI_ZHANG("击掌", 280),
    
    /**
     * 无眼笑
     */
    WU_YAN_XIAO("无眼笑", 281),
    
    /**
     * 敬礼
     */
    JING_LI("敬礼", 282),
    
    /**
     * 狂笑
     */
    KUANG_XIAO("狂笑", 283),
    
    /**
     * 面无表情
     */
    MIAN_WU_BIAO_QING("面无表情", 284),
    
    /**
     * 摸鱼
     */
    MO_YU("摸鱼", 285),
    
    /**
     * 魔鬼笑
     */
    MO_GUI_XIAO("魔鬼笑", 286),
    
    /**
     * 哦
     */
    O("哦", 287),
    
    /**
     * 请
     */
    QING("请", 288),
    
    /**
     * 睁眼
     */
    ZHENG_YAN("睁眼", 289),
    
    /**
     * 敲开心
     */
    QIAO_KAI_XIN("敲开心", 290),
    
    /**
     * 震惊
     */
    ZHEN_JING("震惊", 291),
    
    /**
     * 让我康康
     */
    RANG_WO_KANG_KANG("让我康康", 292),
    
    /**
     * 摸锦鲤
     */
    MO_JIN_LI("摸锦鲤", 293),
    
    /**
     * 期待
     */
    QI_DAI("期待", 294),
    
    /**
     * 拿到红包
     */
    NA_DAO_HONG_BAO("拿到红包", 295),
    
    /**
     * 真好
     */
    ZHEN_HAO("真好", 296),
    
    /**
     * 拜谢
     */
    BAI_XIE("拜谢", 297),
    
    /**
     * 元宝
     */
    YUAN_BAO("元宝", 298),
    
    /**
     * 牛啊
     */
    NIU_A("牛啊", 299),
    
    /**
     * 胖三斤
     */
    PANG_SAN_JIN("胖三斤", 300),
    
    /**
     * 好闪
     */
    HAO_SHAN("好闪", 301),
    
    /**
     * 左拜年
     */
    ZUO_BAI_NIAN("左拜年", 302),
    
    /**
     * 右拜年
     */
    YOU_BAI_NIAN("右拜年", 303),
    
    /**
     * 红包包
     */
    HONG_BAO_BAO("红包包", 304),
    
    /**
     * 右亲亲
     */
    YOU_QIN_QIN("右亲亲", 305),
    
    /**
     * 牛气冲天
     */
    NIU_QI_CHONG_TIAN("牛气冲天", 306),
    
    /**
     * 喵喵
     */
    MIAO_MIAO("喵喵", 307),
    
    /**
     * 求红包
     */
    QIU_HONG_BAO("求红包", 308),
    
    /**
     * 谢红包
     */
    XIE_HONG_BAO("谢红包", 309),
    
    /**
     * 新年烟花
     */
    XIN_NIAN_YAN_HUA("新年烟花", 310),
    
    /**
     * 打call(
     */
    DA_CALL("打call", 311),
    
    /**
     * 变形
     */
    BIAN_XING("变形", 312),
    
    /**
     * 嗑到了
     */
    KE_DAO_LE("嗑到了", 313),
    
    /**
     * 仔细分析
     */
    ZI_XI_FEN_XI("仔细分析", 314),
    
    /**
     * 加油
     */
    JIA_YOU("加油", 315),
    
    /**
     * 我没事
     */
    WO_MEI_SHI("我没事", 316),
    
    /**
     * 菜狗
     */
    CAI_GOU("菜狗", 317),
    
    /**
     * 崇拜
     */
    CHONG_BAI("崇拜", 318),
    
    /**
     * 比心
     */
    BI_XIN("比心", 319),
    
    /**
     * 庆祝
     */
    QING_ZHU("庆祝", 320),
    
    /**
     * 老色批
     */
    LAO_SE_PI("老色批", 321),
    
    /**
     * 拒绝
     */
    JU_JUE("拒绝", 322),
    
    /**
     * 嫌弃
     */
    XIAN_QI("嫌弃", 323),
    
    /**
     * 吃糖
     */
    CHI_TANG("吃糖", 324);
    
    /**
     * 表情名
     */
    private final String name;
    
    /**
     * 表情码
     */
    private final int code;
    
    /**
     * 缓存的复合消息
     */
    private CompoundMessage compoundMessage;
    
    /**
     * code -> expression 哈希表
     */
    private static final Map<Integer, Face> CODE_EXPRESSIONS = new HashMap<>();
    
    /**
     * name -> expression 哈希表
     */
    private static final Map<String, Face> NAME_EXPRESSIONS = new HashMap<>();
    
    /**
     * 构造一个指定 ID 和名字的表情
     *
     * @param name 表情名
     * @param code 表情 ID
     */
    Face(String name, int code) {
        Preconditions.nonNull(name, "name");
    
        this.name = name;
        this.code = code;
    }
    
    static {
        // build instance map
        for (Face face : values()) {
            CODE_EXPRESSIONS.put(face.code, face);
            NAME_EXPRESSIONS.put(face.name, face);
        }
    }
    
    /**
     * 由表情名获得表情实例
     *
     * @param name 表情名
     * @return 若存在该表情，返回该表情，否则返回 null
     */
    public static Face of(String name) {
        Preconditions.nonNull(name, "name");
        
        return NAME_EXPRESSIONS.get(name);
    }
    
    /**
     * 由表情码获得表情实例
     *
     * @param name 表情名
     * @return 若存在该表情，返回该表情，否则返回 null
     */
    public static Face of(int code) {
        return CODE_EXPRESSIONS.get(code);
    }
    
    @Override
    public String serializeToMessageCode() {
        return MessageCode.builder("face")
            .argument(code)
            .build();
    }
    
    @Override
    public CompoundMessage asCompoundMessage() {
        if (Objects.isNull(compoundMessage)) {
            compoundMessage = new SingletonCompoundMessage(this);
        }
        return compoundMessage;
    }
    
    @Override
    public String serializeToSummary() {
        return "[" + name + "]";
    }
}