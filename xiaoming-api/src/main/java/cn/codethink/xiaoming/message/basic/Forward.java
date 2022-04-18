package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;

import java.util.List;

/**
 * <h1>合并转发</h1>
 *
 * <p>表示合并转发的消息。</p>
 *
 * <h2>显示方案</h2>
 *
 * <p>格式：
 * <pre>{@code
 *     |----------------------|
 *     | $title               |
 *     | $preview[0]          |
 *     | $preview[1]          |
 *     | $preview[2]          |
 *     |----------------------|
 *     | $description         |
 *     |----------------------|
 * }</pre></p>
 *
 * <p>例如：
 * <pre>{@code
 *     |----------------------|
 *     | 群聊的消息记录         |
 *     | CHUANWISE: hey!      |
 *     | CHUANWISE: ha!       |
 *     | CODETHINK: hey!      |
 *     |----------------------|
 *     | 查看转发消息          |
 *     |----------------------|
 * }</pre></p>
 *
 * <ul>
 *     <li>消息码：{@code [forward:$title:$description:$source:$summary:$preview:$elements]}</li>
 *     <li>摘要：{@code $summary}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see ForwardBuilder
 * @see ForwardElement
 */
public interface Forward
    extends SingletonMessage, Iterable<ForwardElement>, AutoSerializable, AutoSummarizable {
    
    /**
     * 构造合并转发消息构建器
     *
     * @return 合并转发消息构建器
     */
    static ForwardBuilder builder() {
        return XiaoMing.get().newForwardBuilder();
    }
    
    /**
     * 获取转发标题。
     * 例如「群聊的转发消息」。
     *
     * @return 转发标题
     */
    String getTitle();
    
    /**
     * 获取描述。
     * 例如「查看 3 条转发消息」。
     *
     * @return 描述
     */
    String getDescription();
    
    /**
     * 获取源头。
     * 尚不知道该字符串再何处显示。
     *
     * @return 源头
     */
    String getSource();
    
    /**
     * 获取摘要。
     * 例如「[群聊的消息记录]」。
     *
     * @return 摘要
     */
    String getSummary();
    
    /**
     * 获取预览信息。
     * 一般是消息元素的前 3 条信息。
     * 用于在封面显示。
     *
     * @return 预览信息
     */
    List<String> getPreview();
    
    /**
     * 获取消息内容。
     *
     * @return 消息内容
     */
    List<ForwardElement> getElements();
}
