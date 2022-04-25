package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>提及所有账号</h1>
 *
 * <ul>
 *     <li>消息码：{@code [at:account:all]}</li>
 *     <li>摘要：{@code $description}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see At
 * @see At
 * @see SingletonAt
 */
public interface AtAll
    extends At, AutoSummarizable, AutoSerializable {
    
    /**
     * 获取提及所有成员消息
     *
     * @return 提及所有成员消息
     */
    static AtAll getInstance() {
        return XiaoMing.get().getAllAccountAt();
    }
}
