package cn.codethink.xiaoming.logger;

import cn.codethink.common.util.Arguments;
import cn.codethink.common.util.Preconditions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * 简单日志器，通过 {@link System#out} 输出日志
 *
 * @author Chuanwise
 */
public class SimpleLogger
        extends AbstractLogger {

    public SimpleLogger(String name) {
        super(name);
    }

    public SimpleLogger(String name, LoggerLevel level) {
        super(name, level);
    }
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private String formatNow() {
        return DATE_FORMAT.format(System.currentTimeMillis());
    }

    /**
     * @see Logger#log(LoggerLevel, Object, Object...)
     */
    @Override
    public void log(LoggerLevel level, Object message, Object... arguments) {
        Preconditions.namedArgumentNonNull(level, "logger level");
        Preconditions.namedArgumentNonNull(arguments, "logger arguments");

        if (this.level.compareTo(level) > 0) {
            return;
        }
    
        System.out.println("[" + formatNow() + "] " + name + " | " + level.getName() + " : " + Arguments.format(Objects.toString(message), arguments));
    }
}