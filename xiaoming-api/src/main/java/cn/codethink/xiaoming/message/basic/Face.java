package cn.codethink.xiaoming.message.basic;

/**
 * <h1>表情</h1>
 *
 * <p>用于表示某种表情，如商城表情、原生表情以及其他特殊表情。</p>
 *
 * <ul>
 *     <li>消息码：{@code [face:$type:$value...]}</li>
 *     <li>摘要：{@code [$name]}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see PrimitiveFace
 * @see MarketFace
 */
public interface Face
    extends BasicMessage {
    
    /**
     * 获取消息名
     *
     * @return 消息名
     */
    String getName();
}
