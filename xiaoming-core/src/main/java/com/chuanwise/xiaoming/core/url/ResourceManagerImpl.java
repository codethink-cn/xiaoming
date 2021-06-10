package com.chuanwise.xiaoming.core.url;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.resource.ResourceManager;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.ExternalResource;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
@NoArgsConstructor
public class ResourceManagerImpl extends JsonFilePreservable implements ResourceManager {
    transient File imagesDirectory;

    @Setter
    transient XiaomingBot xiaomingBot;

    @Override
    public Logger getLog() {
        return log;
    }

    Map<String, Long> imageLastVisitTimes = new HashMap<>();

    @Override
    public Message useResources(Message message) {
        message.getMessageChain().replaceAll(singleMessage -> {
            if (singleMessage instanceof Image) {
                return getImage(((Image) singleMessage).getImageId(), message.getContact());
            } else {
                return singleMessage;
            }
        });
        return message;
    }

    @Override
    public Message saveResources(Message message) throws IOException {
        for (SingleMessage singleMessage : message.getMessageChain()) {
            if (singleMessage instanceof Image) {
                final Image image = (Image) singleMessage;
                if (Objects.isNull(getImage(image.getImageId()))) {
                    saveImage(image);
                }
            }
        }
        return message;
    }

    @Override
    public Image getImage(String id, XiaomingContact contact) {
        final File image = getImage(id);
        if (Objects.nonNull(image)) {
            return ExternalResource.uploadAsImage(image, contact.getMiraiContact());
        } else {
            return Image.fromId(id);
        }
    }

    @Override
    public File saveImage(Image image) throws IOException {
        URL url = new URL(Image.queryUrl(image));
        final InputStream inputStream = url.openConnection().getInputStream();

        final File imageFile = new File(imagesDirectory, image.getImageId());
        if (!imageFile.isFile()) {
            imageFile.createNewFile();
        }

        int packSize = 1024;
        byte[] bytes = new byte[packSize];
        int len = 0;
        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        }
        inputStream.close();

        imageLastVisitTimes.put(image.getImageId(), System.currentTimeMillis());
        getXiaomingBot().getFinalizer().readySave(this);
        return imageFile;
    }

    @Override
    public File getImage(String id) {
        final File file = new File(imagesDirectory, id);
        if (file.exists()) {
            imageLastVisitTimes.put(id, System.currentTimeMillis());
            getXiaomingBot().getFinalizer().readySave(this);
            return file;
        } else {
            return null;
        }
    }
}