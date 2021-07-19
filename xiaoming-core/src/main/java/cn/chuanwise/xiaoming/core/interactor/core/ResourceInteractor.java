package cn.chuanwise.xiaoming.core.interactor.core;

import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.annotation.Permission;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.resource.ResourceManager;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import cn.chuanwise.xiaoming.api.utility.CommandWords;
import cn.chuanwise.xiaoming.api.utility.InteractorUtils;
import cn.chuanwise.xiaoming.core.interactor.InteractorImpl;

import java.util.Map;

public class ResourceInteractor extends InteractorImpl {
    final static String RESOURCE = "(资源|resource)";
    final static String BEFORE = "(前|之前|早于|before)";
    final ResourceManager resourceManager;

    public ResourceInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        resourceManager = xiaomingBot.getResourceManager();
    }

    @Filter(RESOURCE)
    @Permission("resource.look")
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
            user.setProperty("earliestVisit", TimeUtility.toTimeLength(System.currentTimeMillis() - earliestVisitTime));
            user.setProperty("latestVisit", TimeUtility.toTimeLength(System.currentTimeMillis() - latestVisitTime));
            user.sendMessage("{howManyImageResourceSaved}，" +
                    "{howLongAfterVisitTheImageWhoHasNotBeenVisitForLongestTime}，" +
                    "{howLongAfterVisitTheImageWhoHasNotBeenVisitForShortestTime}");
        }
    }

    @Filter(CommandWords.REMOVE + RESOURCE)
    @Permission("resource.remove")
    public void onRemoveBefore(XiaomingUser user) {
        user.sendMessage("要删除多久之前的资源？");

        final long before = TimeUtility.parseTimeLength(InteractorUtils.waitNextLegalInput(user, string -> {
            return TimeUtility.parseTimeLength(string) != -1;
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