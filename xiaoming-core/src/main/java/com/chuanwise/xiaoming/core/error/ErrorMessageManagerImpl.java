package com.chuanwise.xiaoming.core.error;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.error.ErrorMessage;
import com.chuanwise.xiaoming.api.error.ErrorMessageManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Slf4j
public class ErrorMessageManagerImpl extends JsonFilePreservable implements ErrorMessageManager {
    transient XiaomingBot xiaomingBot;

    List<ErrorMessageImpl> errorMessages = new ArrayList<>();

    @Override
    public List<ErrorMessage> getErrorMessages() {
        return ((List) errorMessages);
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public void addErrorMessage(ErrorMessage errorMessage) {
        errorMessages.add((ErrorMessageImpl) errorMessage);
    }

    @Override
    public void addThrowableMessage(XiaomingUser user, Throwable throwable) {
        if (Objects.nonNull(throwable.getCause())) {
            throwable = throwable.getCause();
        }
        final ErrorMessage errorMessage;
        if (user.inGroup()) {
            errorMessage = new ErrorMessageImpl(user.getGroup().getId(), user.getQQ(), user.getRecentInputs(), throwable.toString());
        } else {
            errorMessage = new ErrorMessageImpl(user.getQQ(), user.getRecentInputs(), throwable.toString());
        }
        addErrorMessage(errorMessage);
        getXiaomingBot().getResponseGroupManager().sendMessageToTaggedGroup("log", "发现一个新的异常报告");
    }
}
