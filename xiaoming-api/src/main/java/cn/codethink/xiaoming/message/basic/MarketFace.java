package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.message.Summarizable;

/**
 * 商城表情
 *
 * @author Chuanwise
 */
@IMRelatedAPI
public interface MarketFace
    extends BasicMessage, Summarizable {
    
    /**
     * 获取商城表情名
     *
     * @return 商城表情名
     */
    String getName();
}
