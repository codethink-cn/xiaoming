package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.GroupInteractor;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.response.ResponseGroupImpl;
import net.mamoe.mirai.contact.Group;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 全局指令处理器
 * @author Chuanwise
 */
public class GlobalCommandInteractor extends CommandInteractorImpl {
    public static final String ENABLE_TAG = "enable";
    public static final String BAT_REGEX = "(批处理|bat)";

    public GlobalCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    /**
     * 批处理指令
     * @param user 指令执行者
     * @param remain 指令
     */
    @Filter(BAT_REGEX + "{remain}")
    public void onMultipleCommands(XiaomingUser user,
                                   @FilterParameter("remain") String remain) {
        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);

        user.enableBuffer();
        int commandNumber = 0;
        try {
            for (int i = 0; i < subCommands.length; i++) {
                String command = subCommands[i];
                if (command.isEmpty()) {
                    continue;
                }
                user.setMessage(command);
                if (getXiaomingBot().getInteractorManager().onCommand(user)) {
                    commandNumber++;
                } else {
                    user.sendError("无效的命令：{}，批处理任务被中断。", command);
                    break;
                }
            }
        } catch (Exception exception) {
            user.sendError("执行{}个指令时出现异常，批处理任务被中断。");
            exception.printStackTrace();
        }

        final String bufferString = user.getBufferAndClear();
        user.sendPrivateMessage(bufferString);

        if (commandNumber == 0) {
            user.sendError("小明没能成功执行任何一个指令");
        } else {
            user.sendMessage("成功执行了 {} 个指令", commandNumber);
        }
    }

    /**
     * 启动小明
     * @param user 启动人
     */
    @GroupInteractor
    @Filter(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX + CommandWords.ENABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("group.enable")
    public void onEnableXiaoming(XiaomingUser user) {
        final Group group = user.getGroup();
        final ResponseGroupManager responseGroupManager = getXiaomingBot().getResponseGroupManager();

        ResponseGroup responseGroup = responseGroupManager.fromCode(group.getId());
        if (Objects.isNull(responseGroup)) {
            responseGroup = new ResponseGroupImpl(group.getId(), group.getName());
            responseGroupManager.addGroup(responseGroup);
            getXiaomingBot().getRegularPreserveManager().readySave(responseGroupManager);
        }

        if (responseGroup.hasTag(ENABLE_TAG)) {
            user.sendWarning("本群已经是小明的响应群了哦");
        } else {
            responseGroup.addTag(ENABLE_TAG);
            user.sendMessage("成功在本群启用小明 (๑•̀ㅂ•́)و✧");
            getXiaomingBot().getRegularPreserveManager().readySave(responseGroupManager);
        }
    }

    /**
     * 关闭小明
     * @param user 关闭人
     */
    @GroupInteractor
    @Filter(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX + CommandWords.DISABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("group.disable")
    public void onDisableXiaoming(XiaomingUser user) {
        final Group group = user.getAsGroupMember().getGroup();
        final ResponseGroupManager responseGroupManager = getXiaomingBot().getResponseGroupManager();

        ResponseGroup responseGroup = responseGroupManager.fromCode(group.getId());
        if (Objects.isNull(responseGroup)) {
            user.sendMessage("本群还不是小明的响应群哦");
        }

        if (responseGroup.hasTag(ENABLE_TAG)) {
            responseGroup.removeTag(ENABLE_TAG);
            user.sendMessage("本群不再是小明的响应群啦。未来希望启动小明输入 #启动小明 就可以啦。");
        } else {
            user.sendWarning("本群曾是小明的响应群，但是现在还不是哦");
            getXiaomingBot().getRegularPreserveManager().readySave(responseGroupManager);
        }
    }
}
