package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.io.File;
import java.util.Objects;

public class TextCommandInteractor extends CommandInteractorImpl {
    static final String TEXT_REGEX = "(文本|文字|text|txt)";
    final TextManager textManager;

    public TextCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        textManager = xiaomingBot.getTextManager();
    }

    @Filter(TEXT_REGEX)
    @RequirePermission("text.list")
    public void onListText(XiaomingUser user) {
        final File[] files = textManager.list();
        if (files.length == 0) {
            user.sendMessage("没有存储的文本");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("小明一共存储了").append(files.length).append("个文本：");

            for (File file : files) {
                builder.append("\n").append(file.getName());
            }
            user.sendMessage(builder.toString());
        }
    }

    @Filter(TEXT_REGEX + " {name}")
    @RequirePermission("text.look")
    public void onListText(XiaomingUser user, @FilterParameter("name") String name) {
        final String text = textManager.load(name);
        if (Objects.isNull(text)) {
            user.sendMessage("找不到文本：{}", name);
        } else if (text.isEmpty()) {
            user.sendMessage("该文本文件为空");
        } else {
            user.sendMessage(text);
        }
    }

    @Filter(CommandWords.EDIT_REGEX + TEXT_REGEX + " {name}")
    @RequirePermission("text.edit")
    public void onEditText(XiaomingUser user, @FilterParameter("name") String name) {
        final String text = textManager.load(name);
        if (Objects.isNull(text)) {
            user.sendMessage("该文本尚未创建，告诉我它的内容吧~");
            final String nextInput = user.nextInput();
            textManager.save(name, nextInput);
            user.sendMessage("成功新建并保存文本：{}", name);
        } else {
            user.sendMessage("该文本已经存在了，你希望将它修改成什么内容呢？如果不希望修改了告诉我「返回」");
            final String nextInput = user.nextInput();
            if (Objects.equals(nextInput, "返回")) {
                user.sendMessage("已取消本次修改");
            } else {
                textManager.save(name, nextInput);
                user.sendMessage("成功编辑文本：{}", name);
            }
        }
    }
}
