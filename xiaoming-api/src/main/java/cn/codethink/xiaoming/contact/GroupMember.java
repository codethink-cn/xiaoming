package cn.codethink.xiaoming.contact;

import java.util.concurrent.TimeUnit;

/**
 * 群成员
 *
 * @author Chuanwise
 */
public interface GroupMember
    extends Member, GroupSender {
    
    /**
     * 获取群员所在的群聊。
     *
     * @return 群员所在的群聊
     */
    @Override
    Group getMass();
    
    /**
     * 设置该成员的特殊头衔。
     * 只有群主才可以修改成员的特殊头衔。
     *
     * @param title 特殊头衔
     * @throws cn.codethink.xiaoming.exception.PermissionDeniedException 缺少权限时
     */
    void setSpecialTitle(String title);
    
    /**
     * 获取群名片。
     *
     * @return 群名片
     */
    @Override
    String getMassNick();
    
    /**
     * 修改群名片。
     * 只有群主或管理员才可以修改成员的群名片。
     *
     * @param massNick 群名片
     * @throws cn.codethink.xiaoming.exception.PermissionDeniedException 缺少权限时
     */
    void setMassNick(String massNick);
    
    /**
     * 获取群成员的角色
     *
     * @return 成员角色
     */
    Role getRole();
    
    /**
     * 设置群成员的角色。
     * 仅支持设置为 {@link Role#MEMBER} 或 {@link Role#ADMIN}，
     * 暂不支持转让群主（设置为 {@link Role#OWNER}）。
     *
     * @param role 新角色
     * @throws cn.codethink.xiaoming.exception.PermissionDeniedException 缺少权限时
     */
    void setRole(Role role);
    
    /**
     * 禁言一定时间。
     *
     * 在 QQ，若禁言时长小于 1 分钟，仍然将显示为 1 分钟，但不影响真实禁言效果。
     * 若禁言时长大于 29 天，则抛出异常。
     *
     * 若参数为 0，表示解禁。
     *
     * @param time 禁言时长（毫秒）
     * @throws IllegalArgumentException 时间不合法时
     */
    void mute(long time);
    
    /**
     * 禁言一定时间。
     *
     * 在 QQ，若禁言时长小于 1 分钟，仍然将显示为 1 分钟，但不影响真实禁言效果。
     * 若禁言时长大于 29 天，则抛出异常。
     *
     * 若参数为 0，表示解禁。
     *
     * @param time 时间
     * @param timeUnit 时间单位
     * @throws IllegalArgumentException 时间不合法时
     */
    void mute(long time, TimeUnit timeUnit);
    
    /**
     * 解除禁言
     */
    void unmute();
    
    /**
     * 获取剩余禁言时长
     *
     * @return 剩余禁言时长（毫秒）
     */
    long getMuteTimeRemaining();
    
    /**
     * 询问是否正在被禁言
     *
     * @return 是否正在被禁言
     */
    boolean isMuted();
    
    /**
     * 在群聊中发送拍一拍消息
     */
    void flapInGroup();
    
    /**
     * 在私聊中发送拍一拍消息
     */
    void flapInMember();
}
