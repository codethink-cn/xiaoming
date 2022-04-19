package cn.codethink.xiaoming.message.basic;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>提及单个账号</h1>
 *
 * <p>单个账户提及是<b>通讯软件相关</b>的。有些平台使用数字作为编号，有些平台使用文本。</p>
 *
 * <p>消息码：{@code [at:account:singleton:$code]}</p>
 *
 * @author Chuanwise
 */
public interface SingletonAccountAt
    extends AccountAt, AutoSerializable, AutoSummarizable {
    
    /**
     * 构造提及单人账号消息
     *
     * @param targetCode 账号码
     * @return 提及单人账号消息
     * @throws NullPointerException targetCode 为 null
     */
    static SingletonAccountAt newInstance(Code targetCode) {
        Preconditions.objectNonNull(targetCode, "target code");
        
        return XiaoMing.get().newSingletonAccountAt(targetCode);
    }
    
    /**
     * 获取目标账号码
     *
     * @return 目标账号码
     */
    Code getTargetCode();
}
