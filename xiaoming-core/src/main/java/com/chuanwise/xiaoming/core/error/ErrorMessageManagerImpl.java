package com.chuanwise.xiaoming.core.error;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.error.ErrorMessage;
import com.chuanwise.xiaoming.api.error.ErrorMessageManager;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.contact.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class ErrorMessageManagerImpl extends JsonFilePreservable implements ErrorMessageManager {
    transient XiaomingBot xiaomingBot;
    transient Logger log = LoggerFactory.getLogger(getClass());

    List<ErrorMessage> errorMessages = new ArrayList<>();

    @Override
    public void addErrorMessage(ErrorMessage errorMessage) {
        errorMessages.add(errorMessage);
    }

    @Override
    public void addGroupThrowableMessage(GroupXiaomingUser user, Throwable throwable) {
        if (Objects.nonNull(throwable.getCause())) {
            throwable = throwable.getCause();
        }
        ErrorMessage errorMessage = new ErrorMessageImpl(user.getGroupNumber(), user.getQQ(), user.getRecentInputs(), throwable.toString());
        addErrorMessage(errorMessage);
        getXiaomingBot().getResponseGroupManager().sendMessageToTaggedGroup("log", "发现一个新的群异常报告");
    }

    @Override
    public void addThrowableMessage(XiaomingUser user, Throwable throwable) {
        if (Objects.nonNull(throwable.getCause())) {
            throwable = throwable.getCause();
        }
        ErrorMessage errorMessage = new ErrorMessageImpl(user.getQQ(), user.getRecentInputs(), throwable.toString());
        addErrorMessage(errorMessage);
        getXiaomingBot().getResponseGroupManager().sendMessageToTaggedGroup("log", "发现一个新的异常报告");
    }
}
