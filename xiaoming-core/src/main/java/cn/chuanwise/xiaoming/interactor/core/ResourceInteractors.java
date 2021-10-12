package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.util.TimeUtil;
import cn.chuanwise.xiaoming.annotation.Name;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.resource.ResourceManager;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.util.InteractorUtil;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;

import java.util.Map;

public class ResourceInteractors extends SimpleInteractors {
    ResourceManager resourceManager;

    @Override
    public void onRegister() {
        resourceManager = xiaomingBot.getResourceManager();
    }

    @Name("lookResource")
    @Filter(CommandWords.RESOURCE)
    @Permission("resource.look")
    public void onLookResource(XiaomingUser user) {
        final Map<String, Long> imageVisitTimes = resourceManager.getImageLastVisitTimes();
        if (imageVisitTimes.isEmpty()) {
            user.sendMessage("{lang.noAnyImageResource}");
        } else {
            long earliestVisitTime = Long.MAX_VALUE;
            long latestVisitTime = Long.MIN_VALUE;
            for (Long value : imageVisitTimes.values()) {
                latestVisitTime = Math.max(latestVisitTime, value);
                earliestVisitTime = Math.min(earliestVisitTime, value);
            }

            user.sendMessage("{lang.imageResourceDetail}", imageVisitTimes.size(), earliestVisitTime, latestVisitTime);
        }
    }

    @Name("removeBefore")
    @Filter(CommandWords.REMOVE + CommandWords.RESOURCE)
    @Permission("resource.remove")
    public void onRemoveBefore(XiaomingUser user) {
        user.sendMessage("{lang.queryDeleteResourceBefore}");

        final long before = TimeUtil.parseTimeLength(InteractorUtil.waitNextLegalInput(user, string -> {
            return TimeUtil.parseTimeLength(string).isPresent();
        }, "这并不是一个合理的时间长度哦").serialize()).get();

        final int removedNumber = resourceManager.removeBefore(System.currentTimeMillis() - before);
        if (removedNumber > 0) {
            user.sendMessage("{lang.someResourceRemoved}", removedNumber);
            getXiaomingBot().getFileSaver().readyToSave(resourceManager);
        } else {
            user.sendWarning("{lang.noAnyResourceRemoved}");
        }
    }
}