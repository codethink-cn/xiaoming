package cn.codethink.xiaoming.message.module.summary;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.AutoSummarizable;

/**
 * 摘要上下文
 *
 * @author Chuanwise
 *
 * @see Summarizer
 * @see SummaryHandler
 */
public interface SummaryContext
    extends BotObject {
    
    /**
     * 获取摘要对象
     *
     * @return 摘要对象
     */
    AutoSummarizable getSource();
    
    /**
     * 获取相关会话
     *
     * @return 相关会话，或 null
     */
    Contact getContact();
    
    /**
     * 获取相关会话
     *
     * @return 相关会话
     * @throws java.util.NoSuchElementException 获取失败时
     */
    default Contact getContactOrFail() {
        final Contact contact = getContact();
        Preconditions.elementNonNull(contact, "no contact present");
        return contact;
    }
    
    /**
     * 获取相关 bot
     *
     * @return 相关 bot 或 null
     */
    @Override
    default Bot getBot() {
        return getContactOrFail().getBot();
    }
}
