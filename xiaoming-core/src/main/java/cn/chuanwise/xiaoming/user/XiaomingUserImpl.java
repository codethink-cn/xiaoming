package cn.chuanwise.xiaoming.user;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.recept.ReceptionTask;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 小明的使用者对象
 * @author Chuanwise
 */
public abstract class XiaomingUserImpl<C extends XiaomingContact<?>>
        extends ModuleObjectImpl implements XiaomingUser<C> {
    @Getter
    @Setter
    Receptionist receptionist;

    @Getter
    @Setter
    ReceptionTask<XiaomingUser<C>> receptionTask;

    @Getter
    InteractorContext interactorContext;

    @Override
    public void setInteractorContext(InteractorContext interactorContext) {
        ConditionUtil.checkCallerSuperClass(InteractorHandler.class);
        this.interactorContext = interactorContext;
    }

    public XiaomingUserImpl(XiaomingBot xiaomingBot, long qq) {
        super(xiaomingBot);
        this.receptionist = getXiaomingBot().getReceptionistManager().getReceptionist(qq);

        setProperty(PropertyType.QQ, qq);
        setProperty(PropertyType.AT, new At(qq));
    }

    @Override
    public boolean onNextMessage(MessageChain messages) {
        return onNextMessage(new MessageImpl(xiaomingBot, messages));
    }

    /** 发送消息的缓存机制 */
    Stack<StringWriter> stringWriters = new Stack<>();
    Stack<PrintWriter> printWriters = new Stack<>();

    @Override
    public boolean isUsingBuffer() {
        return !stringWriters.empty();
    }

    @Override
    public void enablePrintWriter() {
        final StringWriter stringWriter = new StringWriter();
        stringWriters.add(stringWriter);
        printWriters.add(new PrintWriter(stringWriter));
    }

    @Override
    public void disablePrintWriter() {
        if (isUsingBuffer()) {
            stringWriters.pop();
            printWriters.pop();
        }
    }

    @Override
    public PrintWriter getPrintWriter() {
        if (printWriters.empty()) {
            return null;
        } else {
            return printWriters.peek();
        }
    }

    @Override
    public void appendBuffer(String string) {
        final PrintWriter printWriter = getPrintWriter();
        if (stringWriters.peek().getBuffer().length() != 0) {
            printWriter.println();
        }
        printWriter.print(string);
    }

    @Override
    public String getBufferAndClose() {
        if (stringWriters.empty()) {
            return null;
        }

        final String string = stringWriters.pop().toString();
        disablePrintWriter();

        if (!stringWriters.empty()) {
            stringWriters.peek().append("\n").append(string);
        }

        return string;
    }

    @Override
    public Map<PropertyType, Object> getProperties() {
        return getReceptionist().getProperties();
    }

    @Override
    public <T> Container<T> getProperty(PropertyType<T> type) {
        return Optional.ofNullable(receptionist)
                .map(e -> e.getProperty(type))
                .orElseGet(Container::empty);
    }

    @Override
    public <T> Container<T> waitProperty(PropertyType<T> type, long timeout) throws InterruptedException {
        if (Objects.nonNull(receptionist)) {
            return receptionist.waitProperty(type, timeout);
        } else {
            return Container.empty();
        }
    }

    @Override
    public <T> Container<T> removeProperty(PropertyType<T> type) {
        return Optional.ofNullable(receptionist)
                .map(r -> r.removeProperty(type))
                .orElseGet(Container::empty);
    }

    @Override
    public <T> void setProperty(PropertyType<T> type, T value) {
        if (Objects.nonNull(receptionist)) {
            receptionist.setProperty(type, value);
        }
    }

    @Getter
    Set<Thread> globalMessageWaiter = new CopyOnWriteArraySet<>();
}