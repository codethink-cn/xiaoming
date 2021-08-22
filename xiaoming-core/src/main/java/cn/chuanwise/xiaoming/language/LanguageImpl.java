package cn.chuanwise.xiaoming.language;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

import java.beans.Transient;
import java.util.*;

@Data
public class LanguageImpl extends FilePreservableImpl implements Language {
    transient XiaomingBot xiaomingBot;
    transient Plugin plugin;

    @Override
    @Transient
    public XiaomingBot getXiaomingBot() {
        return xiaomingBot;
    }

    @Override
    @Transient
    public Plugin getPlugin() {
        return plugin;
    }

    Map<String, Sentence> sentences = new HashMap<>();
}
