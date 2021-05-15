package com.chuanwise.xiaoming.host.runnable;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import com.chuanwise.xiaoming.host.user.ConsoleXiaomingUser;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleListenerRunnable extends HostObjectImpl implements Runnable {
    static final Pattern PRIVATE_PATTERN = Pattern.compile("p(rivate)?\\s+(?<qq>\\d+)\\s+(?<content>[\\s\\S]+)");
    static final Pattern GROUP_PATTERN = Pattern.compile("g(roup)?\\s+(?<group>\\d+)\\s+(?<qq>\\d+)\\s+(?<content>[\\s\\S]+)");
    static final Pattern TEMP_PATTERN = Pattern.compile("t(emp)?\\s+(?<group>\\d+)\\s+(?<qq>\\d+)\\s+(?<content>[\\s\\S]+)");

    boolean warned = false;

    public ConsoleListenerRunnable(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public void run() {
        final ConsoleXiaomingUser user = (ConsoleXiaomingUser) getXiaomingBot().getConsoleXiaomingUser();
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
                            user.setAsPrivate(friend);
                            camouflaged = true;
                            user.setMessage(privateMatcher.group("content"));
                        } else {
                            user.sendError("小明没有找到好友 {} 哦", qq);
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
                                    user.setAsGroupMember(miraiBotMember);
                                    camouflaged = true;
                                    user.setMessage(matcher.group("content"));
                                } else {
                                    user.sendError("小明没有在 QQ 群 {} 中找到用户 {} 哦", miraiBotGroup.getName(), qq);
                                }
                            } else {
                                user.sendError("小明没有找到好友 {} 哦", qq);
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
                                    user.setAsGroupMember(miraiBotMember);
                                    camouflaged = true;
                                    user.setMessage(matcher.group("content"));
                                } else {
                                    user.sendError("小明没有在 QQ 群 {} 中找到用户 {} 哦", miraiBotGroup.getName(), qq);
                                }
                            } else {
                                user.sendError("小明没有找到好友 {} 哦", qq);
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (!camouflaged) {
                    if (!warned) {
                        user.sendWarn("不伪装时，身份默认为小明本人和自己的私聊");
                        warned = true;
                    }
                    user.setAsPrivate(miraiBot.getAsFriend());
                    user.setMessage(message);
                }

                try {
                    if (!getXiaomingBot().getInteractorManager().onCommand(user)) {
                        user.sendError("小明不知道你的意思 {}", getXiaomingBot().getWordManager().get("error"));
                    }
                } catch (Exception exception) {
                    if (exception instanceof InterruptedException) {
                        return;
                    } else {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
}
