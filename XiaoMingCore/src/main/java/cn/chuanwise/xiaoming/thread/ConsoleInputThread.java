package cn.chuanwise.xiaoming.thread;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import lombok.Getter;
import lombok.Setter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
@Setter
public class ConsoleInputThread extends ModuleObjectImpl implements Runnable {
    ConsoleXiaomingUser user;
    Receptionist consoleReceptionist;

    volatile Thread thread;

    public ConsoleInputThread(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    public void setUser(ConsoleXiaomingUser user) {
        this.user = user;
        consoleReceptionist = user.getReceptionist();
    }

    private void jline() throws IOException, InterruptedException {
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

                if (Objects.equals("stop", line)) {
                    xiaomingBot.stop();
                    break;
                }

                final String finalLine = line;
                xiaomingBot.getScheduler().run(() -> {
                    final boolean interacted = xiaomingBot.getInteractorManager().interact(user, finalLine);
                    if (!interacted) {
                        user.sendError("小明不知道你的意思「" + finalLine + "」");
                    }
                    return interacted;
                });
            } catch (UserInterruptException exception) {
                break;
            } catch (Exception exception) {
                getLogger().error("执行指令「" + line + "」时出现异常", exception);
            }
        }
    }

    @Override
    public void run() {
        try {
            thread = Thread.currentThread();
            jline();
        } catch (InterruptedException exception) {
            getLogger().info("成功关闭控制台", exception);
        } catch (IOException exception) {
            getLogger().error("启动控制台时出现异常", exception);
        }
    }
}
