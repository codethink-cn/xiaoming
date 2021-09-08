package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.toolkit.functional.throwable.ThrowableRunnable;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.center.content.NormalBotContent;
import cn.chuanwise.xiaoming.client.CenterClient;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import net.mamoe.mirai.message.data.At;

import java.util.Set;
import java.util.stream.Collectors;

public class CenterInteractors extends SimpleInteractors {
    CenterClient centerClient;

    @Override
    public void onRegister() {
        centerClient = xiaomingBot.getCenterClient();
    }

    protected void runIfConnectedOrWarn(XiaomingUser user, String operation, ThrowableRunnable<Exception> runnable) {
        if (centerClient.isConnected()) {
            try {
                runnable.throwableRun();
            } catch (Exception exception) {
                user.sendError("{lang.failBecauseException}", operation, exception);
            }
        } else {
            user.sendError("{lang.notYetConnected}");
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.XIAOMING)
    @Permission("center.list.group")
    public void onListSameGroupXiaoming(GroupXiaomingUser user) {
        runIfConnectedOrWarn(user, "获取本群其他小明信息", () -> {
            final Set<NormalBotContent> sameGroupBots = centerClient.getSameGroupBots(user.getGroupCode());
            if (sameGroupBots.isEmpty()) {
                user.sendMessage("{lang.noAnyOtherOnlineBot}");
            } else {
                user.sendMessage("{lang.sameGroupOnlineBotsAre}", sameGroupBots.stream()
                        .map(bot -> (bot.getName() + "：" + new At(bot.getCode()).serializeToMiraiCode()))
                        .collect(Collectors.toList()));
            }
        });
    }
}
