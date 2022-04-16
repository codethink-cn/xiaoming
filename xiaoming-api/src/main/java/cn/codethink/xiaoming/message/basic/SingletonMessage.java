package cn.codethink.xiaoming.message.basic;

/**
 * 在一个 {@link cn.codethink.xiaoming.message.compound.CompoundMessage} 中最多只能出现一次的基础消息。
 * 如骰子 {@link Dice} 和音乐分享 {@link MusicShare} 等等。这类消息最多再增加一些元数据，如消息源。
 *
 * @author Chuanwise
 */
public interface SingletonMessage
    extends BasicMessage {
}
