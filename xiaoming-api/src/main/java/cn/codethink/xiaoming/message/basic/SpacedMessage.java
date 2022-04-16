package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.Message;

/**
 * 前后有间隔空格的消息。
 * 实现了该接口的消息，将在调用类似 plus 方法时自动在前后添加空格。
 *
 * 若同时实现了 {@link SingletonMessage}，则该接口不生效。
 *
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.compound.CompoundMessageBuilder#plus(Message)
 */
public interface SpacedMessage
    extends BasicMessage {
}
