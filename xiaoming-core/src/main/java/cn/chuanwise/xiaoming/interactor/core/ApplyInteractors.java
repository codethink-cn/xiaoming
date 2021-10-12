package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.verify.VerifyCodeHandler;
import cn.chuanwise.toolkit.verify.VerifyCodeManager;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Name;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.apply.ApplyHandler;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplyInteractors extends SimpleInteractors {
    public static VerifyCodeManager<ApplyHandler> MANAGER;

    protected static final String DENY_GLOBAL_APPLY = "denyGlobalApply";
    protected static final String ACCEPT_GLOBAL_APPLY = "acceptGlobalApply";
    protected static final String IGNORE_GLOBAL_APPLY = "ignoreGlobalApply";

    protected static final String ACCEPT_MY_APPLY = "acceptMyApply";
    protected static final String DENY_MY_APPLY = "denyMyApply";
    protected static final String IGNORE_MY_APPLY = "ignoreMyApply";

    @Override
    public void onRegister() {
        MANAGER = buildVerifyCodeManager();

        xiaomingBot.getInteractorManager().registerParameterParser(VerifyCodeHandler.class, context -> {
            final XiaomingUser user = context.getUser();
            final String inputValue = context.getInputValue();

            switch (context.getInteractor().getName()) {
                case ACCEPT_GLOBAL_APPLY:
                case DENY_GLOBAL_APPLY:
                case IGNORE_GLOBAL_APPLY:
                    final Map<String, ApplyHandler> acceptableGlobalHandlers = forAcceptableInstances(MANAGER, user);
                    if (acceptableGlobalHandlers.isEmpty()) {
                        if (MANAGER.getHandlers().isEmpty()) {
                            user.sendError("{lang.noAnyGlobalApply}");
                        } else {
                            user.sendError("{lang.noAnyAcceptableGlobalApply}");
                        }
                        return null;
                    } else if (acceptableGlobalHandlers.size() == 1) {
                        final ApplyHandler target = acceptableGlobalHandlers.values().iterator().next();

                        if (StringUtil.isEmpty(inputValue)) {
                            // 检查是否是省略参数的
                            return Container.of(target);
                        } else if (Objects.equals(inputValue, target.getVerifyCode())) {
                            return Container.of(target);
                        } else {
                            user.sendError("{lang.noSuchGlobalApplyButSingle}", inputValue, target);
                            return null;
                        }
                    } else {
                        final ApplyHandler target = acceptableGlobalHandlers.get(inputValue);
                        if (Objects.isNull(target)) {
                            user.sendError("{lang.noSuchGlobalApplyButMultiple}", inputValue);
                            return null;
                        } else {
                            return Container.of(target);
                        }
                    }
                default:
                    final VerifyCodeManager<ApplyHandler> personalManager = getVerifyCodeManager(user);
                    final Map<String, ApplyHandler> personalHandlers = personalManager.getHandlers();

                    if (personalHandlers.isEmpty()) {
                        user.sendError("{lang.noAnyPersonalApply}");
                        return null;
                    } else if (personalHandlers.size() == 1) {
                        final ApplyHandler target = personalHandlers.values().iterator().next();

                        if (StringUtil.isEmpty(inputValue)) {
                            return Container.of(target);
                        } else if (Objects.equals(target.getVerifyCode(), inputValue)) {
                            return Container.of(target);
                        } else {
                            user.sendError("{lang.noSuchPersonalApplyButSingle}", inputValue, target);
                            return null;
                        }
                    } else {
                        final ApplyHandler target = personalHandlers.get(inputValue);
                        if (Objects.isNull(target)) {
                            user.sendError("{lang.noSuchPersonalApplyButMultiple}", inputValue);
                            return null;
                        } else {
                            return Container.of(target);
                        }
                    }
            }
        }, true, null);
    }

    protected VerifyCodeManager<ApplyHandler> buildVerifyCodeManager() {
        final Configuration configuration = xiaomingBot.getConfiguration();
        return new VerifyCodeManager(configuration.getVerifyCodeCharacters(), configuration.getMaxVerifyCodeLength());
    }

    protected VerifyCodeManager<ApplyHandler> getVerifyCodeManager(XiaomingUser xiaomingUser) {
        return xiaomingUser.getProperty(PropertyType.VERIFY_CODE_MANAGER).orElseGet(this::buildVerifyCodeManager);
    }

    protected Map<String, ApplyHandler> forAcceptableInstances(VerifyCodeManager<ApplyHandler> manager, XiaomingUser user) {
        final Map<String, ApplyHandler> results = new HashMap<>();
        manager.getHandlers().forEach((key, instance) -> {
            if (user.hasPermissions(instance.getPermissions())) {
                results.put(key, instance);
            }
        });
        return results;
    }

    @Name(ACCEPT_MY_APPLY)
    @Filter(CommandWords.ACCEPT + CommandWords.APPLY + " {验证码}")
    @Filter(CommandWords.ACCEPT + CommandWords.PERSONAL + CommandWords.APPLY + " {验证码}")
    @Permission("apply.personal.accept.mine")
    public void onAcceptMyApply(XiaomingUser user, @FilterParameter("验证码") VerifyCodeHandler handler) {
        if (handler.isSet()) {
            user.sendError("{lang.applyAlreadySet}", handler);
        } else {
            handler.accept();
            user.sendMessage("{lang.applyAccepted}", handler);
        }
    }

    @Name(DENY_MY_APPLY)
    @Filter(CommandWords.DENY + CommandWords.APPLY + " {验证码}")
    @Filter(CommandWords.DENY + CommandWords.PERSONAL + CommandWords.APPLY + " {验证码}")
    @Permission("apply.personal.deny.mine")
    public void onDenyMyApply(XiaomingUser user, @FilterParameter("验证码") VerifyCodeHandler handler) {
        if (handler.isSet()) {
            user.sendError("{lang.applyAlreadySet}", handler);
        } else {
            handler.accept();
            user.sendMessage("{lang.applyDenied}", handler);
        }
    }

    @Name(IGNORE_MY_APPLY)
    @Filter(CommandWords.IGNORE + CommandWords.APPLY + " {验证码}")
    @Filter(CommandWords.IGNORE + CommandWords.PERSONAL + CommandWords.APPLY + " {验证码}")
    @Permission("apply.personal.ignore.mine")
    public void onIgnoreMyApply(XiaomingUser user, @FilterParameter("验证码") VerifyCodeHandler handler) {
        if (handler.isSet()) {
            user.sendError("{lang.applyAlreadySet}", handler);
        } else {
            handler.accept();
            user.sendMessage("{lang.applyIgnored}", handler);
        }
    }

    @Name(ACCEPT_GLOBAL_APPLY)
    @Filter(CommandWords.ACCEPT + CommandWords.GLOBAL + CommandWords.APPLY + " {验证码}")
    @Permission("apply.personal.accept.mine")
    public void onAcceptGlobalApply(XiaomingUser user, @FilterParameter("验证码") VerifyCodeHandler handler) {
        if (handler.isSet()) {
            user.sendError("{lang.applyAlreadySet}", handler);
        } else {
            handler.accept();
            user.sendMessage("{lang.applyAccepted}", handler);
        }
    }

    @Name(DENY_GLOBAL_APPLY)
    @Filter(CommandWords.DENY + CommandWords.GLOBAL + CommandWords.APPLY + " {验证码}")
    @Permission("apply.global.deny.mine")
    public void onDenyGlobalApply(XiaomingUser user, @FilterParameter("验证码") VerifyCodeHandler handler) {
        if (handler.isSet()) {
            user.sendError("{lang.applyAlreadySet}", handler);
        } else {
            handler.accept();
            user.sendMessage("{lang.applyDenied}", handler);
        }
    }

    @Name(IGNORE_GLOBAL_APPLY)
    @Filter(CommandWords.IGNORE + CommandWords.GLOBAL + CommandWords.APPLY + " {验证码}")
    @Permission("apply.global.ignore.mine")
    public void onIgnoreGlobalApply(XiaomingUser user, @FilterParameter("验证码") VerifyCodeHandler handler) {
        if (handler.isSet()) {
            user.sendError("{lang.applyAlreadySet}", handler);
        } else {
            handler.accept();
            user.sendMessage("{lang.applyIgnored}", handler);
        }
    }
}