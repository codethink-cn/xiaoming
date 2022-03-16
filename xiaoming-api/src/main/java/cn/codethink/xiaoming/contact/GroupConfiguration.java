package cn.codethink.xiaoming.contact;

/**
 * 群设置项
 *
 * @author Chuanwise
 */
public interface GroupConfiguration {
    
    /**
     * 询问群名
     *
     * @return 群名
     */
    String getName();
    
    /**
     * 设置群名
     *
     * @param name 群名
     */
    void setName(String name);
    
    /**
     * 询问是否允许群员邀请加入
     *
     * @return 是否允许群员邀请加入
     */
    boolean isAllowMemberInvite();
    
    /**
     * 设置是否允许群员邀请加入
     *
     * @param allowMemberInvite 是否允许群员邀请加入
     */
    void setAllowMemberInvite(boolean allowMemberInvite);
    
    /**
     * 询问是否允许匿名发言
     *
     * @return 是否允许匿名发言
     */
    boolean isAllowAnonymousChat();
    
    /**
     * 设置是否允许匿名发言
     *
     * @param allowAnonymousChat 是否允许匿名发言
     */
    void setAllowAnonymousChat(boolean allowAnonymousChat);
    
    /**
     * 询问是否自动通过入群申请
     *
     * @return 是否自动通过入群申请
     */
    boolean isAutoAcceptMemberAddRequest();
    
//    /**
//     * 设置是否自动通过入群申请
//     *
//     * @param autoAcceptMemberAddRequest 是否自动通过入群申请
//     */
//    void setAutoAcceptMemberAddRequest(boolean autoAcceptMemberAddRequest);
    
    /**
     * 询问是否正在全员禁言
     *
     * @return 是否正在全员禁言
     */
    boolean isMuteAll();
    
    /**
     * 设置是否正在全员禁言
     *
     * @param muteAll 是否正在全员禁言
     */
    void setMuteAll(boolean muteAll);
}
