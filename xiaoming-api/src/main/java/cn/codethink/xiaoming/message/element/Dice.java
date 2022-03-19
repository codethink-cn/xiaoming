package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * 骰子
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class Dice
        extends MarketExpression {
    
    protected final int value;
    
    public Dice(Code code, String name, int value) {
        super(code, name);
    
        Preconditions.argument(value >= 0 && value <= 6);
        
        this.value = value;
    }
}
