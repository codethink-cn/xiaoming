package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

/**
 * MiraiBot 工厂
 */
public class MiraiBotFactory
        extends StaticUtilities {
    
    /**
     * 创建一个现有 Bot 的适配器
     *
     * @param miraiBot 现有的 Bot
     * @return MiraiBot
     */
    public static Bot adapterOf(net.mamoe.mirai.Bot miraiBot) {
        Preconditions.namedArgumentNonNull(miraiBot, "mirai bot");
        
        return new MiraiBot(miraiBot, true);
    }
    
    /**
     * 创建一个新的 Bot
     *
     * @param qq qq
     * @param password 密码
     * @return 尚未启动的 Bot
     */
    public static Bot newBot(long qq, String password) {
        Preconditions.namedArgumentNonEmpty(password, "password");
    
        final net.mamoe.mirai.Bot bot = BotFactory.INSTANCE.newBot(qq, password);
        return new MiraiBot(bot, false);
    }
    
    /**
     *
     * @param qq qq
     * @param md5 密码 MD5 值
     * @return 尚未启动的 Bot
     */
    public static Bot newBot(long qq, byte[] md5) {
        Preconditions.namedArgumentNonNull(md5, "password md5");
    
        final net.mamoe.mirai.Bot bot = BotFactory.INSTANCE.newBot(qq, md5);
        return new MiraiBot(bot, false);
    }
}
