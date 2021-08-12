package cn.chuanwise.xiaoming.language;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.Language;
import lombok.Data;

import java.util.*;

@Data
public class LanguageImpl extends FilePreservableImpl implements Language {
    Map<String, Sentence> sentences = new HashMap<>();
}
