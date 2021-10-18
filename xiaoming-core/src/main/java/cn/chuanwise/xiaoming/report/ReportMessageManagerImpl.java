package cn.chuanwise.xiaoming.report;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Slf4j
public class ReportMessageManagerImpl extends AbstractPreservable implements ReportMessageManager {
    transient XiaomingBot xiaomingBot;

    List<ReportMessage> reportMessages = new ArrayList<>();

    @Transient
    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public void addMessage(ReportMessage reportMessage) {
        reportMessages.add((ReportMessageImpl) reportMessage);
    }

    @Override
    public void addThrowableMessage(Throwable throwable) {
        if (Objects.nonNull(throwable.getCause())) {
            throwable = throwable.getCause();
        }
        addMessage(new ReportMessageImpl(throwable.toString()));
    }

    @Override
    public void addThrowableMessage(XiaomingUser user, Throwable throwable) {
        if (Objects.nonNull(throwable.getCause())) {
            throwable = throwable.getCause();
        }
        final ReportMessage reportMessage;
        final String message = user.getInteractorContext().getMessage().serialize();

        if (user instanceof GroupXiaomingUser) {
            reportMessage = new ReportMessageImpl(((GroupXiaomingUser) user).getGroupCode(), user.getCode(), message, throwable.toString());
        } else {
            reportMessage = new ReportMessageImpl(user.getCode(), message, throwable.toString());
        }
        addMessage(reportMessage);
    }
}
