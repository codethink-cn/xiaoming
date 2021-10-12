package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.MiraiCodeUtil;
import net.mamoe.mirai.message.data.Image;

import java.util.List;

public class DebugInteractors extends SimpleInteractors {
    @Filter("上传 {r:图片}")
    public void onUpload(XiaomingUser user, @FilterParameter("图片") String imageStrings) {
        final List<Image> images = MiraiCodeUtil.getImages(imageStrings);
        if (images.isEmpty()) {
            user.sendError("empty");
        } else {
            user.sendMessage(CollectionUtil.toIndexString(images, Image::queryUrl));
        }
    }
}
