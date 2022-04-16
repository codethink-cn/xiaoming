package cn.codethink.xiaoming.message.element;

import cn.chuanwise.common.util.Indexes;
import cn.chuanwise.common.util.Randoms;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.SingletonCompoundMessage;
import lombok.Data;
import lombok.Getter;

import java.util.Objects;

/**
 * 骰子
 *
 * @author Chuanwise
 */
public enum Dice
    implements MarketFace {
    
    /**
     * 值为 1 的骰子
     */
    ONE(1),
    
    /**
     * 值为 2 的骰子
     */
    TWO(2),
    
    /**
     * 值为 3 的骰子
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
     * 骰子的摘要
     */
    private static final String SUMMARY = "[骰子]";
    
    /**
     * 骰子的名称
     */
    private static final String NAME = "骰子";
    
    /**
     * 复合消息内容
     */
    private SingletonCompoundMessage compoundMessage;
    
    /**
     * 构造一个指定值的骰子
     *
     * @param value 骰子的值
     * @throws IllegalArgumentException value 不合法
     */
    Dice(int value) {
        Preconditions.argument(value >= 1 && value <= 6);
        
        this.value = value;
    }
    
    /**
     * 根据骰子值获得骰子
     *
     * @param code 骰子值
     * @return 骰子
     * @throws IndexOutOfBoundsException code < 0 或 code >= 6
     */
    public static Dice of(int code) {
        Preconditions.objectIndex(code, 6, "dice index");
        return values()[code];
    }
    
    /**
     * 随机获取一个骰子
     *
     * @return 一个骰子
     */
    public static Dice random() {
        final Dice[] values = values();
        final int index = Randoms.nextInt(values.length);
        return values[index];
    }
    
    @Override
    public String serializeToMessageCode() {
        return "[dice:" + value + "]";
    }
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String serializeToSummary() {
        return SUMMARY;
    }
    
    @Override
    public CompoundMessage asCompoundMessage() {
        if (Objects.isNull(compoundMessage)) {
            compoundMessage = new SingletonCompoundMessage(this);
        }
        return compoundMessage;
    }
}