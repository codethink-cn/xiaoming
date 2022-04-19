package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Maps;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.chuanwise.common.space.Pair;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.spi.XiaoMing;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 大手指的戳一戳消息
 *
 * @author Chuanwise
 */
// for ignore name warnings
@SuppressWarnings("all")
@Getter
public enum Poke
    implements SingletonMessage, AutoSerializable, AutoSummarizable {
    
    /**
     * 戳一戳
     */
    ChuoYiChuo("戳一戳", 1, -1),
    
    /**
     * 比心
     */
    BiXin("比心", 2, -1),
    
    /**
     * 点赞
     */
    DianZan("点赞", 3, -1),
    
    /**
     * 心碎
     */
    XinSui("心碎", 4, -1),
    
    /**
     * 666
     */
    LiuLiuLiu("666", 5, -1),
    
    /**
     * 放大招
     */
    FangDaZhao("放大招", 6, -1),
    
    /**
     * 宝贝球
     */
    BaoBeiQiu("宝贝球", 126, 2011),
    
    /**
     * 玫瑰花
     */
    Rose("玫瑰花", 126, 2007),
    
    /**
     * 召唤术
     */
    ZhaoHuanShu("召唤术", 126, 2006),
    
    /**
     * 让你皮
     */
    RangNiPi("让你皮", 126, 2009),
    
    /**
     * 结印
     */
    JieYin("结印", 126, 2005),
    
    /**
     * 手雷
     */
    ShouLei("手雷", 126, 2004),
    
    /**
     * 勾引
     */
    GouYin("勾引", 126, 2003),
    
    /**
     * 抓一下
     */
    ZhuaYiXia("抓一下", 126, 2001),
    
    /**
     * 碎屏
     */
    SuiPing("碎屏", 126, 2002),
    
    /**
     * 敲门
     */
    QiaoMen("敲门", 126, 2002);
    
    /**
     * 戳一戳名
     */
    private final String name;
    
    /**
     * 戳一戳类型
     */
    private final int type;
    
    /**
     * 戳一戳 id
     */
    private final int code;
    
    /**
     * 所有戳一戳消息
     */
    private static final Map<Pair<Integer, Integer>, Poke> INSTANCES = new HashMap<>();
    
    /**
     * 缓存的复合消息
     */
    private CompoundMessage compoundMessage;
    
    static {
        final Poke[] values = values();
        for (Poke value : values) {
            INSTANCES.put(Pair.of(value.type, value.code), value);
        }
    }
    
    Poke(String name, int type, int code) {
        this.name = name;
        this.type = type;
        this.code = code;
    }
    
    /**
     * 根据类型码和 code 获得戳一戳消息
     *
     * @param type 类型码
     * @param code code
     * @return 戳一戳消息
     * @throws java.util.NoSuchElementException 找不到该戳一戳消息
     */
    public static Poke of(int type, int code) {
        return Maps.getOrFail(INSTANCES, Pair.of(type, code));
    }
    
    @Override
    public CompoundMessage asCompoundMessage() {
        if (Objects.isNull(compoundMessage)) {
            compoundMessage = XiaoMing.get().newCompoundMessage(this);
        }
        return compoundMessage;
    }
}
