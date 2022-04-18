package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import lombok.Data;
import net.mamoe.mirai.message.data.ForwardMessage;

/**
 * QQ 商城表情
 *
 * @author Chuanwise
 */
@Data
public class MiraiMarketFace
    extends AbstractBasicMessage
    implements MarketFace, AutoSummarizable, AutoSerializable {
    
    private final int code;
    
    private final String name;
}
