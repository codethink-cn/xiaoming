package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.code.Code;

/**
 * <h1>账户提及</h1>
 *
 * <p>用于提及某个人、某些人或所有人。这种提及在大部分通讯软件中被称为 {@code @} 或艾特。</p>
 *
 * <ul>
 *     <li>消息码：{@code [mention:account:$value...]}</li>
 *     <li>摘要：（取决于具体的类型）</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see Mention
 * @see SingletonAccountMention
 * @see AllAccountMention
 */
@IMRelatedAPI
public interface AccountMention
    extends Mention {
    
    /**
     * 获取提及所有成员消息
     *
     * @return 提及所有成员消息
     */
    static AccountMention all() {
        return AllAccountMention.getInstance();
    }
    
    /**
     * 构造提及单人账号消息
     *
     * @param targetCode 账号码
     * @return 提及单人账号消息
     * @throws NullPointerException targetCode 为 null
     */
    static AccountMention singleton(Code targetCode) {
        return SingletonAccountMention.newInstance(targetCode);
    }
}
