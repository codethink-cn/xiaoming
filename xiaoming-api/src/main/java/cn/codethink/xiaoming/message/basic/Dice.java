package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.spi.XiaoMing;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

/**
 * <h1>骰子</h1>
 *
 * <p>消息码：{@code [dice:$value]}</p>
 * <p>摘要：{@code [骰子]}</p>
 *
 * @author Chuanwise
 */
public enum Dice
    implements MarketFace, SingletonMessage, AutoSerializable, AutoSummarizable {
    
    /**
     * 值为 1 的骰子
     */
    ONE(1),
    
    /**
     * 值为 2 的骰子
     */
    TWO(2),
    
    /**
     * 值为 31 的骰子
     */
    THREE(3),
    
    /**
     * 值为 4 的骰子
     */
    FOUR(4),
    
    /**
     * 值为 5 的骰子
     */
    FIVE(5),
    
    /**
     * 值为 6 的骰子
     */
    SIX(6);
    
    /**
     * 骰子的值
     */
    @Getter
    private final int value;
    
    /**
     * 缓存的复合消息
     */
    private CompoundMessage compoundMessage;
    
    private static final Dice[] INSTANCES = values();
    
    Dice(int value) {
        this.value = value;
    }
    
    /**
     * 获取一个指定值的骰子
     *
     * @param value 骰子值
     * @return 骰子
     * @throws IllegalArgumentException value < 1 或 value > 6
     */
    public static Dice of(int value) {
        Preconditions.element(value >= 1 && value < 6,
            "dice value must be bigger or equals than 1, and smaller or equals than 6!");
    
        return INSTANCES[value - 1];
    }
    
    @Override
    public CompoundMessage asCompoundMessage() {
        if (Objects.isNull(compoundMessage)) {
            compoundMessage = XiaoMing.get().newCompoundMessageBuilder();
        }
        return compoundMessage;
    }
    
    @Override
    public String getName() {
        return "[骰子]";
    }
}