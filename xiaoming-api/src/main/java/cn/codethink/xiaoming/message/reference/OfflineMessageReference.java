package cn.codethink.xiaoming.message.reference;

/**
 * 离线消息源，可能由转发或引用的消息获得。因为可能来自
 * 其他与 Bot 没有关系的用户，因此可能无法打开相关会话。
 *
 * @author Chuanwise
 */
public interface OfflineMessageReference
    extends MessageReference {
}
