package com.chuanwise.xiaoming.api.resource;

import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ResourceManager extends ModuleObject {
    default Message useResources(Message message) {
        message.setMessageChain(useResources(message.getMessageChain(), message.getContact().getMiraiContact()));
        return message;
    }

    MessageChain useResources(MessageChain messages, Contact miraiContact);

    Message saveResources(Message message) throws IOException;

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

    default void removeBefore(long time) {
        getImageLastVisitTimes().entrySet().removeIf(entry -> {
            final String id = entry.getKey();
            final long lastVisitTime = entry.getValue();
            final boolean shouldRemove = time > lastVisitTime;

            return shouldRemove && getImage(id).delete();
        });
    }
}
