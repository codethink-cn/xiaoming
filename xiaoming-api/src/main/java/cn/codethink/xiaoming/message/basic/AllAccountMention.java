package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>提及所有账号</h1>
 *
 * <ul>
 *     <li>消息码：{@code [mention:account:all]}</li>
 *     <li>摘要：{@code $description}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see Mention
 * @see AccountMention
 * @see SingletonAccountMention
 */
public interface AllAccountMention
    extends AccountMention, AutoSummarizable, AutoSerializable {
    
    /**
     * 获取提及所有成员消息
     *
     * @return 提及所有成员消息
     */
    static AllAccountMention getInstance() {
        return XiaoMing.get().getAllAccountMention();
    }
}
