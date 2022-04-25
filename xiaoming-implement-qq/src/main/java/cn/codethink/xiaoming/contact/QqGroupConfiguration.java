package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * Qq 群设置项
 *
 * @author Chuanwise
 */
@Data
public class QqGroupConfiguration
        implements GroupConfiguration {
    
    private final QqGroup group;
    
    public QqGroupConfiguration(QqGroup group) {
        Preconditions.nonNull(group, "group");
        
        this.group = group;
    }
    
    @Override
    public String getName() {
        group.assertBotIsInGroup();
        return group.qqGroup.getName();
    }
    
    @Override
    public void setName(String name) {
        group.assertBotIsInGroup();
        group.qqGroup.setName(name);
    }
    
    @Override
    public boolean isAllowMemberInvite() {
        group.assertBotIsInGroup();
        return group.qqGroup.getSettings().isAllowMemberInvite();
    }
    
    @Override
    public void setAllowMemberInvite(boolean allowMemberInvite) {
        group.assertBotIsInGroup();
        group.qqGroup.getSettings().setAllowMemberInvite(allowMemberInvite);
    }
    
    @Override
    public boolean isAllowAnonymousChat() {
        group.assertBotIsInGroup();
        return group.qqGroup.getSettings().isAnonymousChatEnabled();
    }
    
    @Override
    public void setAllowAnonymousChat(boolean allowAnonymousChat) {
        group.assertBotIsInGroup();
        group.qqGroup.getSettings().setAnonymousChatEnabled(allowAnonymousChat);
    }
    
    @Override
    public boolean isAutoAcceptMemberAddRequest() {
        group.assertBotIsInGroup();
        return group.qqGroup.getSettings().isAutoApproveEnabled();
    }

//    @Override
//    public void setAutoAcceptMemberAddRequest(boolean autoAcceptMemberAddRequest) {
//        group.assertBotIsInGroup();
//        group.qqGroup.getSettings().setA(muteAll);
//    }
    
    @Override
    public boolean isMuteAll() {
        group.assertBotIsInGroup();
        return group.qqGroup.getSettings().isMuteAll();
    }
    
    @Override
    public void setMuteAll(boolean muteAll) {
        group.assertBotIsInGroup();
        group.qqGroup.getSettings().setMuteAll(muteAll);
    }
}