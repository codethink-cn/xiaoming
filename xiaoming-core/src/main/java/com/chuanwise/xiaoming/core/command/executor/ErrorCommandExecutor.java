package com.chuanwise.xiaoming.core.command.executor;

import com.chuanwise.xiaoming.api.annotation.Command;
import com.chuanwise.xiaoming.api.annotation.CommandParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.error.ErrorMessage;
import com.chuanwise.xiaoming.api.error.ErrorMessageManager;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.command.executor.CommandExecutorImpl;

import java.util.List;
import java.util.Objects;

public class ErrorCommandExecutor extends CommandExecutorImpl {
    final ErrorMessageManager errorMessageManager;
    final List<ErrorMessage> errorMessages;

    public ErrorCommandExecutor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        errorMessageManager = getXiaomingBot().getErrorMessageManager();
        errorMessages = errorMessageManager.getErrorMessages();
    }

    public void onShowErrorMessage(XiaomingUser user,
                                   ErrorMessage message) {
        StringBuilder builder = new StringBuilder("【消息详情】").append("\n");
        builder.append(message.getMessage())
                .append("QQ：" + message.getQq());

        if (Objects.nonNull(message.getLastInputs())) {
            builder.append("输入：" + message.getLastInputs());
        }

        if (message.getGroup() != 0) {
            builder.append("群：" + message.getGroup());
        }

        builder.append("时间：" + TimeUtil.FORMAT.format(message.getTime()));

        if (user instanceof GroupXiaomingUser) {
            ((GroupXiaomingUser) user).sendPrivateMessage(builder.toString());
        } else {
            user.sendMessage(builder.toString());
        }
    }

    @Command(CommandWords.RECENT_REGEX + CommandWords.MESSAGE_REGEX)
    @RequirePermission("message.look")
    public void onLookLastMessage(final XiaomingUser user) {
        if (errorMessages.isEmpty()) {
            user.sendMessage("没有未经查看的消息哦");
        } else if (errorMessages.size() == 1) {
            onShowErrorMessage(user, errorMessages.get(0));
            errorMessages.clear();
            getXiaomingBot().getRegularPreserveManager().readySave(errorMessageManager);
        } else {
            StringBuilder builder = new StringBuilder()
                    .append("一共有 ").append(errorMessages.size()).append(" 个未经查看的消息");

            int index = 1;
            for (ErrorMessage errorMessage : errorMessages) {
                String shortMessage = errorMessage.getMessage();
                if (shortMessage.length() > 30) {
                    shortMessage = shortMessage.substring(0, 29) + "...";
                }
                builder.append("\n").append(index++).append("、").append(shortMessage);
            }
            user.sendMessage(builder.toString());
        }
    }

    @Command(CommandWords.RECENT_REGEX + CommandWords.MESSAGE_REGEX + " {index}")
    @RequirePermission("message.look")
    public void onLookMessage(final XiaomingUser user,
                                @CommandParameter("index") final String indexString) {
        if (errorMessages.isEmpty()) {
            user.sendMessage("没有未经查看的消息哦");
        } else if (errorMessages.size() == 1) {
            user.sendMessage("只有一个未经处理的消息，直接使用 #近期消息 就可以啦");
            return;
        }

        final int index;
        if (indexString.matches("\\d+")) {
            index = Integer.parseInt(indexString);
        } else {
            user.sendError("{}并不是一个合理的数字哦", indexString);
            return;
        }

        if (index <= 0 || index > errorMessages.size()) {
            user.sendError("{}不对哦，它应该是介于 1 到 {} 之间的数字", indexString, errorMessages.size());
        } else {
            final ErrorMessage errorMessage = errorMessages.get(index - 1);
            onShowErrorMessage(user, errorMessage);
            errorMessages.remove(errorMessage);
            getXiaomingBot().getRegularPreserveManager().readySave(errorMessageManager);
        }
    }

    @Command(CommandWords.CLEAR_REGEX + CommandWords.RECENT_REGEX + CommandWords.MESSAGE_REGEX)
    @RequirePermission("message.clear")
    public void onClearMessage(final XiaomingUser user) {
        if (errorMessages.isEmpty()) {
            user.sendMessage("并没有需要清除的未经查看的消息哦");
        } else {
            errorMessages.clear();
            getXiaomingBot().getRegularPreserveManager().readySave(errorMessageManager);
            user.sendMessage("成功清除未经查看的消息");
        }
    }
}