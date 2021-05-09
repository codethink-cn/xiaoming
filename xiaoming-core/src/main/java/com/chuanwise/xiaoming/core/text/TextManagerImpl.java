package com.chuanwise.xiaoming.core.text;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;

/**
 * @author Chuanwise
 */
@Data
public class TextManagerImpl extends HostXiaomingObjectImpl implements TextManager {
    final File directory;

    public TextManagerImpl(XiaomingBot xiaomingBot, File directory) {
        super(xiaomingBot);
        this.directory = directory;
    }
}
