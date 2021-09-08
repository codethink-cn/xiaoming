package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Data;
import net.mamoe.mirai.message.code.MiraiCode;

import java.util.Optional;
import java.util.Set;

@Data
public class MessageEvent extends SimpleXiaomingCancellableEvent {
    final XiaomingUser user;
    final Message message;
    boolean interactable;

    public MessageEvent(XiaomingUser user, Message message) {
        setXiaomingBot(user.getXiaomingBot());
        this.user = user;
        this.message = message;
        flushInteractable();
    }

    public boolean isInteractable() {
        return interactable;
    }

    @Override
    public void onCall() {
        flushInteractable();
    }

    protected void flushInteractable() {
        final String serializedMessage = message.serialize();

        if (user instanceof GroupXiaomingUser) {
            final GroupXiaomingUser groupXiaomingUser = (GroupXiaomingUser) user;
            // 如果启动了明确调用
            if (xiaomingBot.getConfiguration().isEnableClearCall() && groupXiaomingUser.getContact().hasTag(xiaomingBot.getConfiguration().getClearCallGroupTag())) {
                final Set<String> clearCallPrefixes = xiaomingBot.getConfiguration().getClearCallPrefixes();

                final Optional<String> optionalPrefix = clearCallPrefixes.stream()
                        .filter(prefix -> serializedMessage.startsWith(prefix) && serializedMessage.length() > prefix.length())
                        .findFirst();

                if (optionalPrefix.isPresent()) {
                    interactable = true;
                    final String finalPrefix = optionalPrefix.get();
                    message.setMessageChain(MiraiCode.deserializeMiraiCode(serializedMessage.substring(finalPrefix.length()), user.getContact().getMiraiContact()));
                } else {
                    interactable = false;
                }
            } else {
                interactable = true;
            }
        } else {
            interactable = true;
        }
    }
}
