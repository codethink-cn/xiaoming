package cn.codethink.xiaoming.contact;

import cn.codethink.util.Preconditions;
import lombok.Data;

/**
 * Mirai 群设置项
 *
 * @author Chuanwise
 */
@Data
public class MiraiGroupConfiguration
        implements GroupConfiguration {
    
    private final MiraiGroup group;
    
    public MiraiGroupConfiguration(MiraiGroup group) {
        Preconditions.namedArgumentNonNull(group, "group");
        
        this.group = group;
    }
    
    @Override
    public String getName() {
        group.assertBotIsInGroup();
        return group.getMiraiGroup().getName();
    }
    
    @Override
    public void setName(String name) {
        group.assertBotIsInGroup();
        group.getMiraiGroup().setName(name);
    }
    
    @Override
    public boolean isAllowMemberInvite() {
        group.assertBotIsInGroup();
        return group.getMiraiGroup().getSettings().isAllowMemberInvite();
    }
    
    @Override
    public void setAllowMemberInvite(boolean allowMemberInvite) {
        group.assertBotIsInGroup();
        group.getMiraiGroup().getSettings().setAllowMemberInvite(allowMemberInvite);
    }
    
    @Override
    public boolean isAllowAnonymousChat() {
        group.assertBotIsInGroup();
        return group.getMiraiGroup().getSettings().isAnonymousChatEnabled();
    }
    
    @Override
    public void setAllowAnonymousChat(boolean allowAnonymousChat) {
        group.assertBotIsInGroup();
        group.getMiraiGroup().getSettings().setAnonymousChatEnabled(allowAnonymousChat);
    }
    
    @Override
    public boolean isAutoAcceptMemberAddRequest() {
        group.assertBotIsInGroup();
        return group.getMiraiGroup().getSettings().isAutoApproveEnabled();
    }
    
//    @Override
//    public void setAutoAcceptMemberAddRequest(boolean autoAcceptMemberAddRequest) {
//        group.assertBotIsInGroup();
//        group.getMiraiGroup().getSettings().setA(muteAll);
//    }
    
    @Override
    public boolean isMuteAll() {
        group.assertBotIsInGroup();
        return group.getMiraiGroup().getSettings().isMuteAll();
    }
    
    @Override
    public void setMuteAll(boolean muteAll) {
        group.assertBotIsInGroup();
        group.getMiraiGroup().getSettings().setMuteAll(muteAll);
    }
}
