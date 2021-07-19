package cn.chuanwise.xiaoming.core.report;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.error.ReportMessage;
import cn.chuanwise.xiaoming.api.error.ReportMessageManager;
import cn.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
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
public class ReportMessageManagerImpl extends FilePreservableImpl implements ReportMessageManager {
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

        final List<String> recentMessages = CollectionUtility.addTo(user.getRecentMessages(), new LinkedList<>(), Message::serialize);
        final List<String> messages = new ArrayList<>(recentMessages.size());
        messages.addAll(recentMessages);

        if (user instanceof GroupXiaomingUser) {
            reportMessage = new ReportMessageImpl(((GroupXiaomingUser) user).getGroupCode(), user.getCode(), messages, throwable.toString());
        } else {
            reportMessage = new ReportMessageImpl(user.getCode(), messages, throwable.toString());
        }
        addMessage(reportMessage);
        getXiaomingBot().getGroupRecordManager().sendMessageToTaggedGroup("log", "发现一个新的异常报告");
    }
}
