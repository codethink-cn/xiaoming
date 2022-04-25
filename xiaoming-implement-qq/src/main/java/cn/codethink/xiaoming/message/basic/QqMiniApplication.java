package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.MiniApplication
 */
@Data
public class QqMiniApplication
    extends AbstractBasicMessage
    implements MiniApplication, AutoSummarizable, AutoSerializable {
    
    private final String content;
}
