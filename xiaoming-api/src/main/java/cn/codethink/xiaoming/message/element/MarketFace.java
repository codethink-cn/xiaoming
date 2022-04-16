package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.message.SummarizableMessage;

/**
 * 商城表情
 *
 * @author Chuanwise
 */
@IMRelatedAPI
public interface MarketFace
    extends BasicMessage, SummarizableMessage {
    
    /**
     * 获取商城表情名
     *
     * @return 商城表情名
     */
    String getName();
}
