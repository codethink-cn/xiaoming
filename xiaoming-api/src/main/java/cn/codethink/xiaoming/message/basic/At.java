package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Summarizable;

/**
 * <h1>账户提及</h1>
 *
 * <p>用于提及某个人、某些人或所有人。这种提及在大部分通讯软件中被称为 {@code @} 或艾特。</p>
 *
 * <ul>
 *     <li>消息码：{@code [at:account:$value...]}</li>
 *     <li>摘要：（取决于具体的类型）</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see At
 * @see SingletonAt
 * @see AtAll
 */
@IMRelatedAPI
public interface At
    extends BasicMessage, Summarizable, SpacedMessage {
    
    /**
     * 获取提及所有成员消息
     *
     * @return 提及所有成员消息
     */
    static At all() {
        return AtAll.getInstance();
    }
    
    /**
     * 构造提及单人账号消息
     *
     * @param targetCode 账号码
     * @return 提及单人账号消息
     * @throws NullPointerException targetCode 为 null
     */
    static At of(Code targetCode) {
        return SingletonAt.newInstance(targetCode);
    }
}
