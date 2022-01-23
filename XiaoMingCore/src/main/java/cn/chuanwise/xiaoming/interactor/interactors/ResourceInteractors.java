package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.TimeUtil;
import cn.chuanwise.xiaoming.annotation.Name;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Required;
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
    @Required("resource.look")
    public void onLookResource(XiaomingUser user) {
        final Map<String, Long> imageVisitTimes = resourceManager.getImageLastVisitTimes();
        if (imageVisitTimes.isEmpty()) {
            user.sendMessage("没有任何图片资源");
        } else {
            long earliestVisitTime = Long.MAX_VALUE;
            long latestVisitTime = Long.MIN_VALUE;
            for (Long value : imageVisitTimes.values()) {
                latestVisitTime = Math.max(latestVisitTime, value);
                earliestVisitTime = Math.min(earliestVisitTime, value);
            }

            user.sendMessage("目前一共有 " + imageVisitTimes.size() + " 个图片资源。\n" +
                    "最久的图片 " + TimeUtil.toTimeLength(System.currentTimeMillis() - earliestVisitTime) + " 没有访问过了。\n" +
                    "最近的一张则是在 " + TimeUtil.toTimeLength(System.currentTimeMillis() - latestVisitTime) + " 前访问过");
        }
    }

    @Name("removeBefore")
    @Filter(CommandWords.REMOVE + CommandWords.RESOURCE)
    @Required("resource.remove")
    public void onRemoveBefore(XiaomingUser user) {
        user.sendMessage("你希望删除多长时间之前的资源呢？");

        final long before = TimeUtil.parseTimeLength(InteractorUtil.waitNextLegalInput(user, string -> {
            return TimeUtil.parseTimeLength(string).isPresent();
        }, "这并不是一个合理的时间长度哦").serialize()).get();

        final int removedNumber = resourceManager.removeBefore(System.currentTimeMillis() - before);
        if (removedNumber > 0) {
            user.sendMessage("成功删除了 " + removedNumber + " 个资源");
            getXiaomingBot().getFileSaver().readyToSave(resourceManager);
        } else {
            user.sendWarning("没有任何资源被删除");
        }
    }
}