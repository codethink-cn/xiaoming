package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.client.CenterClient;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.exception.InteractExitedException;
import cn.chuanwise.xiaoming.recept.Receptionist;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Chuanwise
 */
@Getter
public class GroupXiaomingUserImpl extends XiaomingUserImpl<GroupContact> implements GroupXiaomingUser {
    final GroupContact contact;
    final MemberContact memberContact;

    public GroupXiaomingUserImpl(MemberContact contact) {
        super(contact.getXiaomingBot(), contact.getCode());
        this.contact = contact.getGroupContact();
        this.memberContact = contact;
    }

    @Override
    public long getCode() {
        return memberContact.getCode();
    }

    @Override
    public String getName() {
        return memberContact.getName();
    }

    @Override
    public String getCompleteName() {
        return "「" + contact.getAliasAndCode() + "」" + getName() + "（" + getCodeString() + "）";
    }

    @Override
    public Optional<Message> nextMessage(long timeout) throws InterruptedException, InteractExitedException {
        final Optional<Message> optional = xiaomingBot.getContactManager().nextGroupMemberMessage(getGroupCode(), getCode(), timeout).map(MessageEvent::getMessage);
        if (optional.isPresent()) {
            final Message message = optional.get();
            final String serializedMessage = message.serialize();

            if (Objects.equals(serializedMessage, "退出")) {
                throw new InteractExitedException();
            } else {
                final CommandRecord commandRecord = buildCommandRecord(serializedMessage);
                final Account account = getAccount();
                account.addCommand(commandRecord);
                getXiaomingBot().getFileSaver().readyToSave(account);

                final CenterClient client = getXiaomingBot().getCenterClient();
                client.doOrFail(client::increaseTotalCallNumber, "增加总小明调用次数");

                return optional;
            }
        } else {
            return optional;
        }
    }
}
