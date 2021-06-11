package com.chuanwise.xiaoming.core.resource;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.resource.ResourceManager;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

@Data
@Slf4j
public class ResourceManagerImpl extends JsonFilePreservable implements ResourceManager {
    transient File imagesDirectory;
    transient File resourceDirectory;

    @Setter
    transient XiaomingBot xiaomingBot;

    @Override
    public void setResourceDirectory(File resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
        imagesDirectory = new File(resourceDirectory, "images");
        imagesDirectory.mkdirs();
    }

    @Override
    public Logger getLog() {
        return log;
    }

    Map<String, Long> imageLastVisitTimes = new HashMap<>();

    @Override
    public MessageChain useResources(MessageChain messages, Contact miraiContact) {
        List<SingleMessage> resultMessages = new ArrayList<>(messages.size());
        messages.forEach(singleMessage -> {
            if (singleMessage instanceof Image) {
                resultMessages.add(getImage(((Image) singleMessage).getImageId(), miraiContact));
            } else {
                resultMessages.add(singleMessage);
            }
        });
        final MessageChainBuilder builder = new MessageChainBuilder(resultMessages.size());
        builder.addAll(resultMessages);
        return builder.asMessageChain();
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
    public Image getImage(String id, Contact miraiContact) {
        final File image = getImage(id);
        if (Objects.nonNull(image)) {
            return ExternalResource.uploadAsImage(image, miraiContact);
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
        getXiaomingBot().getScheduler().readySave(this);
        return imageFile;
    }

    @Override
    public File getImage(String id) {
        final File file = new File(imagesDirectory, id);
        if (file.exists()) {
            imageLastVisitTimes.put(id, System.currentTimeMillis());
            getXiaomingBot().getScheduler().readySave(this);
            return file;
        } else {
            return null;
        }
    }
}