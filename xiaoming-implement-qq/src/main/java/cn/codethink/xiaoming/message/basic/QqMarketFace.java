package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import lombok.Data;

/**
 * QQ ๅๅ่กจๆ
 *
 * @author Chuanwise
 */
@Data
public class QqMarketFace
    extends AbstractBasicMessage
    implements MarketFace, AutoSummarizable, AutoSerializable {
    
    private final int code;
    
    private final String name;
}
