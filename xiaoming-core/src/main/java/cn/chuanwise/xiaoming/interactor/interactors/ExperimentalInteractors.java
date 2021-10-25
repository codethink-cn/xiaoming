package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;

public class ExperimentalInteractors extends SimpleInteractors {
    @Filter("重复 {次数} {r:内容}")
    public void onRepeat(XiaomingUser user,
                         @FilterParameter("次数") int times,
                         @FilterParameter("内容") String content) {
        for (int i = 0; i < times; i++) {
            user.sendMessage(content);
        }
    }
}
