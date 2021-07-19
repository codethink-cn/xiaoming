package cn.chuanwise.xiaoming.api.resource;

import cn.chuanwise.xiaoming.api.object.ModuleObject;
import cn.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.toolkit.preservable.Preservable;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public interface ResourceManager extends ModuleObject, Preservable<File> {
    default Message useResources(Message message) {
        message.setMessageChain(useResources(message.getMessageChain(), message.getContact().getMiraiContact()));
        return message;
    }

    MessageChain useResources(MessageChain messages, Contact miraiContact);

    default Message saveResources(Message message) throws IOException {
        saveResources(message.getMessageChain());
        return message;
    }

    default MessageChain saveResources(MessageChain messages) throws IOException {
        for (SingleMessage singleMessage : messages) {
            if (singleMessage instanceof Image) {
                final Image image = (Image) singleMessage;
                if (Objects.isNull(getImage(image.getImageId()))) {
                    saveImage(image);
                }
            }
        }
        return messages;
    }

    default String saveResources(String messages) throws IOException {
        saveResources(MiraiCode.deserializeMiraiCode(messages));
        return messages;
    }

    default Image getImage(String id, XiaomingContact contact) {
        return getImage(id, contact.getMiraiContact());
    }

    Image getImage(String id, Contact miraiContact);

    File saveImage(Image image) throws IOException;

    File getImage(String id);

    File getImagesDirectory();

    Map<String, Long> getImageLastVisitTimes();

    default Long getImageLastVisitTime(String id) {
        return getImageLastVisitTimes().get(id);
    }

    void setResourceDirectory(File resourceDirectory);

    default int removeBefore(long time) {
        int sizeBeforeRemove = getImageLastVisitTimes().size();
        getImageLastVisitTimes().entrySet().removeIf(entry -> {
            final String id = entry.getKey();
            final long lastVisitTime = entry.getValue();
            final boolean shouldRemove = time > lastVisitTime;

            return shouldRemove && getImage(id).delete();
        });
        return sizeBeforeRemove - getImageLastVisitTimes().size();
    }
}
