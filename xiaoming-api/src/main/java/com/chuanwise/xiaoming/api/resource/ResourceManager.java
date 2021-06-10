package com.chuanwise.xiaoming.api.resource;

import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.object.HostObject;
import net.mamoe.mirai.message.data.Image;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ResourceManager extends HostObject {
    Message useResources(Message message);

    Message saveResources(Message message) throws IOException;

    Image getImage(String id, XiaomingContact contact);

    File saveImage(Image image) throws IOException;

    File getImage(String id);

    File getImagesDirectory();

    Map<String, Long> getImageLastVisitTimes();

    default Long getImageLastVisitTime(String id) {
        return getImageLastVisitTimes().get(id);
    }

    void setImagesDirectory(File imagesDirectory);
}
