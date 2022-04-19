package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.annotation.InternalAPI;

/**
 * <h1>服务消息</h1>
 *
 * <p>是通讯软件平台上的特殊消息。如 QQ 的 XML 和 JSON 消息。</p>
 *
 * <p>由于服务消息高度平台相关，API 不提供任何相关接口。</p>
 *
 * @author Chuanwise
 */
@InternalAPI
public interface Service
    extends SingletonMessage {
}
