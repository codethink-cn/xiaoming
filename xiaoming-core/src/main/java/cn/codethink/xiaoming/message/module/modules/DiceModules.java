package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.message.basic.Dice;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;

import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.MarketFace
 */
public class DiceModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // dice
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(value = Dice.class, priority = Priority.HIGH)
    List<String> serializeDice(Dice dice) {
        return Collections.asUnmodifiableList(
            "dice",
            Integer.toString(dice.getValue())
        );
    }
    
    @Deserializer("dice:?")
    Dice deserializeDice(@DeserializerValue String code) {
        return Dice.of(Integer.parseInt(code));
    }
    
    @Summarizer(value = Dice.class, priority = Priority.HIGH)
    String summaryDice() {
        return "[骰子]";
    }
}
