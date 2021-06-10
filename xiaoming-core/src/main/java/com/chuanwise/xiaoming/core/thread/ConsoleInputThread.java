package com.chuanwise.xiaoming.core.thread;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.core.contact.message.TempMessageImpl;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import com.chuanwise.xiaoming.core.recept.ReceptionistImpl;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import lombok.Data;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class ConsoleInputThread extends HostObjectImpl implements Runnable {
    static final Pattern PRIVATE_PATTERN = Pattern.compile("p(rivate)?\\s+(?<qq>\\d+)\\s+(?<content>[\\s\\S]+)");
    static final Pattern GROUP_PATTERN = Pattern.compile("g(roup)?\\s+(?<group>\\d+)\\s+(?<qq>\\d+)\\s+(?<content>[\\s\\S]+)");
    static final Pattern TEMP_PATTERN = Pattern.compile("t(emp)?\\s+(?<group>\\d+)\\s+(?<qq>\\d+)\\s+(?<content>[\\s\\S]+)");

    boolean warned = false;

    ConsoleXiaomingUser consoleUser;
    Receptionist consoleReceptionist;

    volatile Thread inputThread;

    public ConsoleInputThread(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    public void setConsoleUser(ConsoleXiaomingUser consoleUser) {
        this.consoleUser = consoleUser;
        consoleReceptionist = new ReceptionistImpl(consoleUser);
    }

    @Override
    public void run() {
        if (Objects.isNull(inputThread)) {
            inputThread = Thread.currentThread();
        } else {
            throw new XiaomingRuntimeException("multiple console input thread");
        }

        final Bot miraiBot = getXiaomingBot().getMiraiBot();
        boolean camouflaged;

        try (Scanner scanner = new Scanner(System.in)) {
            while (!getXiaomingBot().isStop()) {
                camouflaged = false;
                final String message = scanner.nextLine();

                // 身份伪装
                try {
                    // 尝试伪装成私聊
                    final Matcher privateMatcher = PRIVATE_PATTERN.matcher(message);
                    if (privateMatcher.matches()) {
                        final long qq = Long.parseLong(privateMatcher.group("qq"));
                        final Friend friend = miraiBot.getFriend(qq);
                        if (Objects.nonNull(friend)) {
                            consoleReceptionist.onPrivateMessage(getXiaomingBot().getContactManager().getPrivateContact(qq), privateMatcher.group("content"));
                            camouflaged = true;
                        } else {
                            consoleUser.sendError("小明没有找到好友 {} 哦", qq);
                        }
                    }

                    // 尝试伪装为群聊
                    if (!camouflaged) {
                        final Matcher matcher = GROUP_PATTERN.matcher(message);
                        if (matcher.matches()) {
                            final long qq = Long.parseLong(matcher.group("qq"));
                            final long group = Long.parseLong(matcher.group("group"));
                            final Group miraiBotGroup = miraiBot.getGroup(group);
                            if (Objects.nonNull(miraiBotGroup)) {
                                final NormalMember miraiBotMember = miraiBotGroup.getOrFail(qq);
                                if (Objects.nonNull(miraiBotMember)) {
                                    consoleReceptionist.onGroupMessage(getXiaomingBot().getContactManager().getGroupContact(group), matcher.group("content"));
                                    camouflaged = true;
                                } else {
                                    consoleUser.sendError("小明没有在 QQ 群 {} 中找到用户 {} 哦", miraiBotGroup.getName(), qq);
                                }
                            } else {
                                consoleUser.sendError("小明没有找到好友 {} 哦", qq);
                            }
                        }
                    }

                    // 伪装为临时会话
                    if (!camouflaged) {
                        final Matcher matcher = TEMP_PATTERN.matcher(message);
                        if (matcher.matches()) {
                            final long qq = Long.parseLong(matcher.group("qq"));
                            final long group = Long.parseLong(matcher.group("group"));
                            final Group miraiBotGroup = miraiBot.getGroup(group);
                            if (Objects.nonNull(miraiBotGroup)) {
                                final NormalMember miraiBotMember = miraiBotGroup.getOrFail(qq);
                                if (Objects.nonNull(miraiBotMember)) {
                                    consoleReceptionist.onTempMessage(getXiaomingBot().getContactManager().getTempContact(group, qq), matcher.group("content"));
                                    camouflaged = true;
                                } else {
                                    consoleUser.sendError("小明没有在 QQ 群 {} 中找到用户 {} 哦", miraiBotGroup.getName(), qq);
                                }
                            } else {
                                consoleUser.sendError("小明没有找到好友 {} 哦", qq);
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (!camouflaged) {
                    if (!warned) {
                        consoleUser.sendWarning("不伪装时，身份默认为小明本人和自己的私聊");
                        warned = true;
                    }
                    consoleReceptionist.onPrivateMessage(getXiaomingBot().getContactManager().getPrivateContact(consoleUser.getCode()), message);
                }
            }
        }
    }
}
