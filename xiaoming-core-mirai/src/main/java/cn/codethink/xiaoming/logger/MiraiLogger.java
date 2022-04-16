package cn.codethink.xiaoming.logger;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.MiraiBot;
import org.jetbrains.annotations.Nullable;

/**
 * 小明 Logger 和 MiraiLogger 的适配器
 *
 * @author Chuanwise
 */
public class MiraiLogger
    extends AbstractLogger
    implements net.mamoe.mirai.utils.MiraiLogger {
    
    public static final String NAME = "implement bot";
    
    private final MiraiBot bot;
    
    public MiraiLogger(MiraiBot bot) {
        super(NAME);
        
        Preconditions.nonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    @Override
    public void log(LoggerLevel level, Object message, Object... arguments) {
        Preconditions.nonNull(level, "level");
        Preconditions.nonNull(message, "message");
        Preconditions.nonNull(arguments, "arguments");
    }
    
    @Nullable
    @Override
    public String getIdentity() {
        return NAME;
    }
    
    @Override
    public boolean isEnabled() {
        return !bot.getBotConfiguration().isHideImplementBotLog();
    }
    
    @Override
    public void debug(@Nullable String s) {
        super.debug(s);
    }
    
    @Override
    public void debug(@Nullable String s, @Nullable Throwable throwable) {
        super.debug(s, throwable);
    }
    
    @Override
    public void error(@Nullable String s) {
        super.error(s);
    }
    
    @Override
    public void error(@Nullable String s, @Nullable Throwable throwable) {
        super.debug(s, throwable);
    }
    
    @Override
    public void info(@Nullable String s) {
        super.debug(s);
    }
    
    @Override
    public void info(@Nullable String s, @Nullable Throwable throwable) {
        super.debug(s, throwable);
    }
    
    @Override
    public void verbose(@Nullable String s) {
        super.trace(s);
    }
    
    @Override
    public void verbose(@Nullable String s, @Nullable Throwable throwable) {
        super.trace(s, throwable);
    }
    
    @Override
    public void warning(@Nullable String s) {
        super.warn(s);
    }
    
    @Override
    public void warning(@Nullable String s, @Nullable Throwable throwable) {
        super.warn(s, throwable);
    }
}
