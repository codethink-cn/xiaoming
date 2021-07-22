package cn.chuanwise.xiaoming.user;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.recept.ReceptionTask;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.At;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 小明的使用者对象
 * @author Chuanwise
 */
public abstract class XiaomingUserImpl<C extends XiaomingContact<M, ?>, M extends Message, R extends ReceptionTask>
        extends ModuleObjectImpl implements XiaomingUser<C, M, R> {
    @Setter
    @Getter
    Receptionist receptionist;

    @Setter
    @Getter
    Interactor interactor;

    public XiaomingUserImpl(XiaomingBot xiaomingBot, long qq) {
        super(xiaomingBot);
        this.receptionist = getXiaomingBot().getReceptionistManager().forReceptionist(qq);
        this.properties = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxUserPropertyQuantity());

        setProperty("qq", qq);
        setProperty("at", new At(qq).serializeToMiraiCode());
    }

    /**
     * 发送消息的缓存机制
     */
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

    @Getter
    final Map<String, Object> properties;

    @Getter
    Map<String, Set<Thread>> propertyWaiters = new ConcurrentHashMap<>();

    @Getter
    Set<Thread> globalMessageWaiter = new CopyOnWriteArraySet<>();
}