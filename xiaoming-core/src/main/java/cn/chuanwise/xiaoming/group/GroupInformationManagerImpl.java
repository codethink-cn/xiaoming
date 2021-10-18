package cn.chuanwise.xiaoming.group;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 响应群管理器
 */
@Getter
@NoArgsConstructor
public class GroupInformationManagerImpl extends AbstractPreservable implements GroupInformationManager {
    Set<GroupInformation> groups = new CopyOnWriteArraySet<>();

    @Setter
    transient XiaomingBot xiaomingBot;

    @Override
    public synchronized GroupInformation addGroupInformation(long groupCode) {
        return getGroupInformation(groupCode)
                .orElseGet(() -> {
                    final String groupName = xiaomingBot.getContactManager().getGroupContact(groupCode)
                            .map(GroupContact::getName)
                            .orElse(null);

                    final GroupInformation information = new GroupInformationImpl(groupCode, groupName);
                    if (addGroupInformation(information)) {
                        return information;
                    } else {
                        throw new IllegalStateException();
                    }
                });
    }

    @Override
    public boolean addGroupInformation(GroupInformation information) {
        information.flush();
        final boolean effected = getGroupInformation(information.getCode()).isEmpty();
        if (effected) {
            groups.add(information);
        }
        return effected;
    }
}
