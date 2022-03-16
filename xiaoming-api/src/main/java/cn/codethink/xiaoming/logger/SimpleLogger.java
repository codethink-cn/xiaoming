package cn.codethink.xiaoming.logger;

import cn.codethink.util.Arguments;
import cn.codethink.util.Preconditions;
import org.fusesource.jansi.Ansi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * 简单日志器，通过 {@link System#out} 和 {@link System#err} 输出日志。
 * 当日志等级小于 {@link LoggerLevel#ERROR}，采用 {@link System#out} 输出，
 * 否则采用 {@link System#err} 输出。
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

        switch (level) {
            case ERROR: {
                System.out.println(Ansi.ansi()
                        .fgBrightBlack()
                        .a("[")
                        .a(formatNow())
                        .a("]")
                        .a(" ")
                        .fgBrightRed()
                        .a(name)
                        .a(" ")
                        .fgBrightBlack()
                        .a("|")
                        .a(" ")
                        .fgRed()
                        .a(level.getName())
                        .a(" ")
                        .fgBrightBlack()
                        .a(":")
                        .a(" ")
                        .fgBrightRed()
                        .a(Arguments.replaceAll(Objects.toString(message), arguments))
                        .reset()
                        .toString());
            }
                break;
            case INFO: {
                System.out.println(Ansi.ansi()
                        .fgBrightBlack()
                        .a("[")
                        .a(formatNow())
                        .a("]")
                        .a(" ")
                        .fgBrightCyan()
                        .a(name)
                        .a(" ")
                        .fgBrightBlack()
                        .a("|")
                        .a(" ")
                        .fgCyan()
                        .a(level.getName())
                        .a(" ")
                        .fgBrightBlack()
                        .a(":")
                        .a(" ")
                        .reset()
                        .a(Arguments.replaceAll(Objects.toString(message), arguments))
                        .reset()
                        .toString());
            }
                break;
            case DEBUG: {
                System.out.println(Ansi.ansi()
                        .fgBrightBlack()
                        .a("[")
                        .a(formatNow())
                        .a("]")
                        .a(" ")
                        .fgBrightBlack()
                        .a(name)
                        .a(" ")
                        .fgBrightBlack()
                        .a("|")
                        .a(" ")
                        .fgBrightBlack()
                        .a(level.getName())
                        .a(" ")
                        .fgBrightBlack()
                        .a(":")
                        .a(" ")
                        .fgBrightBlack()
                        .a(Arguments.replaceAll(Objects.toString(message), arguments))
                        .reset()
                        .toString());
            }
                break;
            case WARN: {
                System.out.println(Ansi.ansi()
                        .fgBrightBlack()
                        .a("[")
                        .a(formatNow())
                        .a("]")
                        .a(" ")
                        .fgBrightYellow()
                        .a(name)
                        .a(" ")
                        .fgBrightBlack()
                        .a("|")
                        .a(" ")
                        .fgYellow()
                        .a(level.getName())
                        .a(" ")
                        .fgBrightBlack()
                        .a(":")
                        .a(" ")
                        .fgBrightYellow()
                        .a(Arguments.replaceAll(Objects.toString(message), arguments))
                        .reset()
                        .toString());
            }
                break;
            case FATAL: {
                System.out.println(Ansi.ansi()
                        .fgBrightBlack()
                        .a("[")
                        .a(formatNow())
                        .a("]")
                        .a(" ")
                        .fgBrightMagenta()
                        .a(name)
                        .a(" ")
                        .fgBrightBlack()
                        .a("|")
                        .a(" ")
                        .fgMagenta()
                        .a(level.getName())
                        .a(" ")
                        .fgBrightBlack()
                        .a(":")
                        .a(" ")
                        .fgBrightMagenta()
                        .a(Arguments.replaceAll(Objects.toString(message), arguments))
                        .reset()
                        .toString());
            }
                break;
            default:
            case TRACE: {
                System.out.println(Ansi.ansi()
                        .fgBrightBlack()
                        .a("[")
                        .a(formatNow())
                        .a("]")
                        .a(" ")
                        .fgBrightBlack()
                        .a(name)
                        .a(" ")
                        .fgBrightBlack()
                        .a("|")
                        .a(" ")
                        .fgBrightBlack()
                        .a(level.getName())
                        .a(" ")
                        .fgBrightBlack()
                        .a(":")
                        .a(" ")
                        .fgBrightBlack()
                        .a(Arguments.replaceAll(Objects.toString(message), arguments))
                        .reset()
                        .toString());
            }
        }
    }
}
