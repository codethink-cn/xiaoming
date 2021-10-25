package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.language.variable.VariableHandler;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.Map;
import java.util.Optional;

/**
 * 单词指令处理器
 * @author Chuanwise
 */
public class LanguageIterators
        extends SimpleInteractors {
    LanguageManager languageManager;

    @Override
    public void onRegister() {
        languageManager = getXiaomingBot().getLanguageManager();

        xiaomingBot.getInteractorManager().registerParameterParser(Sentence.class, context -> {
            final String inputValue = context.getInputValue();
            final Optional<Sentence> optionalSentence = languageManager.getSentence(inputValue);
            if (optionalSentence.isEmpty()) {
                context.getUser().sendError("无法找到句子「" + inputValue + "」");
                return null;
            } else {
                return Container.ofOptional(optionalSentence);
            }
        }, true, null);
    }

    @Filter(CommandWords.GLOBAL + CommandWords.VARIABLE)
    @Required("language.variable.list")
    public void listGlobalVariables(XiaomingUser user) {
        final Map<String, VariableHandler> variables = languageManager.getGlobalVariables();
        if (variables.isEmpty()) {
            user.sendError("没有任何全局变量");
        } else {
            user.sendMessage("一共有 " + variables.size() + " 个全局变量：\n" +
                    CollectionUtil.toIndexString(variables.values(),
                            handler -> handler.getName() + "：" + handler.getGetter().get() + "（" + Plugin.getChineseName(handler.getPlugin()) + " 注册）"));
        }
    }

    @Filter(CommandWords.SENTENCE + " {r:句子}")
    @Required("language.sentence.look")
    public void lookSentence(XiaomingUser user, @FilterParameter("句子") Sentence sentence) {
        user.sendMessage("「语句信息」\n" +
                "默认值：" + sentence.getDefaultValue() + "\n" +
                "自定义值：" + Optional.ofNullable(CollectionUtil.toIndexString(sentence.getCustomValues()))
                                .map(x -> "\n" + x)
                                .orElse("（无）"));
    }
}
