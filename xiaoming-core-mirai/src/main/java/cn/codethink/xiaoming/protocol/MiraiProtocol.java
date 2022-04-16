package cn.codethink.xiaoming.protocol;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import cn.codethink.xiaoming.protocol.Protocol;
import net.mamoe.mirai.utils.BotConfiguration;

import java.util.NoSuchElementException;

/**
 * Mirai 协议工具类
 *
 * @author Chuanwise
 */
public class MiraiProtocol
    extends StaticUtilities {
    
    /**
     * 将小明协议转化为 Mirai 协议
     *
     * @param protocol 小明协议
     * @return Mirai 协议
     */
    public static BotConfiguration.MiraiProtocol toMiraiProtocol(Protocol protocol) {
        Preconditions.nonNull(protocol, "protocol");
    
        switch (protocol) {
            case ANDROID_PAD:
                return BotConfiguration.MiraiProtocol.ANDROID_PAD;
            case ANDROID_PHONE:
                return BotConfiguration.MiraiProtocol.ANDROID_PHONE;
            case ANDROID_WATCH:
                return BotConfiguration.MiraiProtocol.ANDROID_WATCH;
            default:
                throw new NoSuchElementException();
        }
    }
    
    /**
     * 将 Mirai 协议转化为小明协议
     *
     * @param protocol Mirai 协议
     * @return 小明协议
     */
    public static Protocol fromMiraiProtocol(BotConfiguration.MiraiProtocol protocol) {
        Preconditions.nonNull(protocol, "protocol");
    
        switch (protocol) {
            case ANDROID_PAD:
                return Protocol.ANDROID_PAD;
            case ANDROID_PHONE:
                return Protocol.ANDROID_PHONE;
            case ANDROID_WATCH:
                return Protocol.ANDROID_WATCH;
            default:
                throw new NoSuchElementException();
        }
    }
}
