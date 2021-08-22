package cn.chuanwise.xiaoming.thread;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessageImpl;
import cn.chuanwise.xiaoming.log.ConsoleLogger;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Future;

@Getter
@Setter
public class ConsoleInputThread extends ModuleObjectImpl implements Runnable {
    boolean warned = false;

    ConsoleXiaomingUser user;
    Receptionist consoleReceptionist;

    volatile Thread inputThread;
    ConsoleLogger consoleLogger;

    public ConsoleInputThread(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    public void setUser(ConsoleXiaomingUser user) {
        this.user = user;
        consoleReceptionist = user.getReceptionist();
    }

    public ConsoleLogger getConsoleLogger() {
        if (Objects.isNull(consoleLogger)) {
            consoleLogger = getXiaomingBot().getFileLoader()
                    .loadOrSupply(ConsoleLogger.class, new File(getXiaomingBot().getLogDirectory(), "console.json"), ConsoleLogger::new);
        }
        return consoleLogger;
    }

    private void jline() throws IOException {
        final Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        final LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        final String prompt = "> ";

        while (true) {
            String line = null;
            try {
                line = lineReader.readLine(prompt);
                final ConsoleMessage message = user.buildMessage(line);

                if (Objects.equals("stop", line)) {
                    xiaomingBot.stop();
                    break;
                }

                if (Objects.isNull(user.getInteractorContext())) {
                    getXiaomingBot().getScheduler().run(() -> {
                        Thread.currentThread().setName("reception-task[console]");
                        final boolean interacted = getXiaomingBot().getInteractorManager().interact(user, message);

                        if (!interacted) {
                            user.sendError("小明不知道你的意思");
                        }
                        return interacted;
                    });
                } else {
                    user.onNextInput(message);
                }
            } catch (UserInterruptException exception) {
                break;
            } catch (Exception exception) {
                getLogger().error("执行指令「" + line + "」时出现异常", exception);
            }
        }
    }

    private void scanner() {
        if (Objects.isNull(inputThread)) {
            inputThread = Thread.currentThread();
        } else {
            throw new XiaomingRuntimeException("multiple console input thread");
        }

        Scanner scanner = new Scanner(System.in);
        while (!getXiaomingBot().isDisabled()) {
            final String message = scanner.nextLine();
            final ConsoleMessage consoleMessage = new ConsoleMessageImpl(user, MiraiCode.deserializeMiraiCode(message));

            try {
                if (Objects.isNull(user.getInteractorContext())) {
                    // 启动新的接待任务
                    final Future<Boolean> future = getXiaomingBot().getScheduler().run(() -> {
                        Thread.currentThread().setName("reception-task[console]");
                        return getXiaomingBot().getInteractorManager().interact(user, consoleMessage);
                    });

                    if (!future.get()) {
                        user.sendError("小明不知道你的意思");
                    }
                } else {
                    user.onNextInput(consoleMessage);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            jline();
        } catch (IOException exception) {
            getLogger().error("启动控制台时出现异常", exception);
        }
    }
}
