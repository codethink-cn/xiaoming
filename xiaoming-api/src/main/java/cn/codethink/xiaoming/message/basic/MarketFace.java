package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.message.Summarizable;

/**
 * <h1>商城表情</h1>
 *
 * <p>表示表情商城中的某些表情，可能由用户制作上传，因此高度平台相关。</p>
 *
 * <ul>
 *     <li>消息码：{@code [face:market:$value...]}</li>
 *     <li>摘要：{@code [$name]}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see Face
 */
@IMRelatedAPI
public interface MarketFace
    extends Face, SingletonMessage {
}
