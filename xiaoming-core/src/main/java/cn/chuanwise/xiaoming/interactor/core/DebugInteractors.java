package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.permission.PermissionUserNode;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.MiraiCodeUtility;
import net.mamoe.mirai.message.data.Image;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DebugInteractors extends SimpleInteractors {
    @Filter("上传 {r:图片}")
    public void onUpload(XiaomingUser user, @FilterParameter("图片") String imageStrings) {
        final List<Image> images = MiraiCodeUtility.getImages(imageStrings);
        if (images.isEmpty()) {
            user.sendError("empty");
        } else {
            user.sendMessage(CollectionUtility.toIndexString(images, Image::queryUrl));
        }
    }
}
