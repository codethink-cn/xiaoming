package com.chuanwise.xiaoming.core.error;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.error.ReportMessage;
import com.chuanwise.xiaoming.api.error.ReportMessageManager;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Slf4j
public class ReportMessageManagerImpl extends JsonFilePreservable implements ReportMessageManager {
    transient XiaomingBot xiaomingBot;

    List<ReportMessageImpl> reportMessages = new ArrayList<>();

    @Override
    public List<ReportMessage> getReportMessages() {
        return ((List) reportMessages);
    }

    @Override
    public Logger getLog() {
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

        final List<String> recentMessages = CollectionUtils.addTo(user.getRecentMessages(), new LinkedList<>(), Message::serialize);
        final List<String> messages = new ArrayList<>(recentMessages.size());
        messages.addAll(recentMessages);

        if (user instanceof GroupXiaomingUser) {
            reportMessage = new ReportMessageImpl(((GroupXiaomingUser) user).getGroupCode(), user.getCode(), messages, throwable.toString());
        } else {
            reportMessage = new ReportMessageImpl(user.getCode(), messages, throwable.toString());
        }
        addMessage(reportMessage);
        getXiaomingBot().getResponseGroupManager().sendMessageToTaggedGroup("log", "发现一个新的异常报告");
    }
}
