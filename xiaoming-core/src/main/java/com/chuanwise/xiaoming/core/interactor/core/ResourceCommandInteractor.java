package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.resource.ResourceManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResourceCommandInteractor extends CommandInteractorImpl {
    final static String RESOURCE = "(资源|resource)";
    final static String BEFORE = "(前|之前|早于|before)";
    final ResourceManager resourceManager;

    public ResourceCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        resourceManager = xiaomingBot.getResourceManager();
    }

    @Filter(RESOURCE)
    @Require("resource.look")
    public void onLookResource(XiaomingUser user) {
        final Map<String, Long> imageVisitTimes = resourceManager.getImageLastVisitTimes();
        if (imageVisitTimes.isEmpty()) {
            user.sendMessage("{noAnyImageResource}");
        } else {
            long earliestVisitTime = Long.MAX_VALUE;
            long latestVisitTime = Long.MIN_VALUE;
            for (Long value : imageVisitTimes.values()) {
                latestVisitTime = Math.max(latestVisitTime, value);
                earliestVisitTime = Math.min(earliestVisitTime, value);
            }

            user.setProperty("size", imageVisitTimes.size());
            user.setProperty("earliestVisit", TimeUtils.toTimeString(System.currentTimeMillis() - earliestVisitTime));
            user.setProperty("latestVisit", TimeUtils.toTimeString(System.currentTimeMillis() - latestVisitTime));
            user.sendMessage("{howManyImageResourceSaved}，" +
                    "{howLongAfterVisitTheImageWhoHasNotBeenVisitForLongestTime}，" +
                    "{howLongAfterVisitTheImageWhoHasNotBeenVisitForShortestTime}");
        }
    }

    @Filter(CommandWords.REMOVE + RESOURCE)
    @Require("resource.remove")
    public void onRemoveBefore(XiaomingUser user) {
        user.sendMessage("要删除多久之前的资源？");

        final long before = TimeUtils.parseTime(InteractorUtils.waitNextLegalInput(user, string -> {
            return TimeUtils.parseTime(string) != -1;
        }, "「{last}」不是一个合理的时间哦").serialize());

        final int removedNumber = resourceManager.removeBefore(System.currentTimeMillis() - before);
        if (removedNumber > 0) {
            user.setProperty("size", removedNumber);
            user.sendMessage("{howManyImageResourceRemoved}");
            getXiaomingBot().getScheduler().readySave(resourceManager);
        } else {
            user.sendWarning("{noAnyImageResourceRemoved}");
        }
    }
}