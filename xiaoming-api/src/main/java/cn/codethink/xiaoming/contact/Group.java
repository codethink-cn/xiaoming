package cn.codethink.xiaoming.contact;

/**
 * 像 QQ 那样的一对多的群，只有一个可以推送消息的区域。
 *
 * @author Chuanwise
 */
public interface Group
        extends Contact, Scope {
    
    /**
     * 询问 Bot 是否还在群里
     *
     * @return Bot 是否还在群里
     */
    boolean isBotInGroup();
    
    /**
     * 获取群设置项
     *
     * @return 群设置项
     */
    GroupConfiguration getConfiguration();
}
