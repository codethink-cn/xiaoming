package cn.codethink.xiaoming.message.module.summary;

import cn.codethink.xiaoming.message.AutoSummarizable;

/**
 * 摘要器
 *
 * @author Chuanwise
 *
 * @see Summarizer
 * @see AutoSummarizable
 * @see cn.codethink.xiaoming.message.Summarizable
 */
public interface SummaryHandler {
    
    /**
     * 获取对象的摘要信息
     *
     * @param context 对象
     * @return 摘要信息
     * @throws NullPointerException context 为 null
     * @throws Exception 摘要过程出现异常
     */
    String summary(SummaryContext context) throws Exception;
}
