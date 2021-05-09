package com.chuanwise.xiaoming.core.interactor;

import com.chuanwise.xiaoming.api.annotation.InteractMethod;
import com.chuanwise.xiaoming.api.error.ErrorMessageManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.error.ErrorMessageImpl;

import java.util.Objects;

public class ErrorReportInteractor extends InteractorImpl {
    @Override
    public boolean willInteract(XiaomingUser user) {
        return Objects.equals(user.getMessage(), "#反馈");
    }

    @InteractMethod
    public void onMessage(XiaomingUser user) {
        user.sendMessage("你遇到了什么问题，或有什么建议呢？赶快告诉小明吧 {}，" +
                "当你说完了，告诉我「结束」就可以啦", getXiaomingBot().getWordManager().get("happy"));

        StringBuilder builder = new StringBuilder();
        String nextInput = user.nextInput();
        while (true) {
            if (Objects.equals(nextInput, "结束")){
                if (builder.length() == 0) {
                    user.sendMessage("本次没有反馈任何信息哦");
                } else {
                    final ErrorMessageManager errorMessageManager = getXiaomingBot().getErrorMessageManager();
                    errorMessageManager.addErrorMessage(new ErrorMessageImpl(user.getQQ(), builder.toString()));
                    getXiaomingBot().getRegularPreserveManager().readySave(errorMessageManager);

                    user.sendMessage("感谢你的反馈，一起期待更好的小明吧 {}", getXiaomingBot().getWordManager().get("happy"));
                    getXiaomingBot().getResponseGroupManager().sendMessageToTaggedGroup("log", "收到一则用户反馈");
                }
                return;
            } else {
                if (builder.length() == 0) {
                    builder.append(nextInput);
                } else {
                    builder.append("\n").append(nextInput);
                }
            }
            nextInput = user.nextInput();
        }
    }
}
