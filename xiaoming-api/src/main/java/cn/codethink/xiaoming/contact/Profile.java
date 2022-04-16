package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.BotObject;

/**
 * 好友资料
 *
 * @author Chuanwise
 */
public interface Profile
    extends BotObject {
    
    /**
     * 获取性别
     *
     * @return 性别
     */
    Sex getSex();
    
    /**
     * 获取年龄
     *
     * @return 年龄
     */
    int getAge();
    
    /**
     * 获取等级
     *
     * @return 等级
     */
    int getLevel();
    
    /**
     * 获取个性签名
     *
     * @return 个性签名
     */
    String getSign();
    
    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    String getEmail();
}
