package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import net.mamoe.mirai.BotFactory;

/**
 * QqBot 工厂
 *
 * @author Chuanwise
 * @see QqBot
 * @see Bot
 */
public class QqBotFactory
        extends StaticUtilities {
    
    /**
     * 创建一个新的 Bot
     *
     * @param qq qq
     * @param password 密码
     * @return 尚未启动的 Bot
     */
    public static Bot newBot(long qq, String password) {
        Preconditions.objectArgumentNonEmpty(password, "password");
    
        final net.mamoe.mirai.Bot bot = BotFactory.INSTANCE.newBot(qq, password);
        return new QqBot(bot);
    }
    
    /**
     *
     * @param qq qq
     * @param md5 密码 MD5 值
     * @return 尚未启动的 Bot
     */
    public static Bot newBot(long qq, byte[] md5) {
        Preconditions.nonNull(md5, "password md5");
    
        final net.mamoe.mirai.Bot bot = BotFactory.INSTANCE.newBot(qq, md5);
        return new QqBot(bot);
    }
}
