package com.chuanwise.xiaoming.api.interactor;

import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 小明的上下文相关交互器
 * @author Chuanwise
 */
public interface Interactor extends XiaomingObject {
    /**
     * 重新统计交互方法详情
     */
    void reloadInteractorDetails(Logger logger);

    /**
     * 和一个用户交互。如果交互成功，返回 true
     * @param user 当前交互用户
     * @return 是否交互成功
     * @throws Exception 交互途中抛出的异常
     */
    boolean interact(XiaomingUser user) throws Exception;

    /**
     * 解析未知的参数
     * @param user 当前交互人
     * @param parameter 无法自动注入的参数
     * @return 注入结果。如果为 {@code null} 则注入失败
     */
    default Object onParameter(XiaomingUser user, Parameter parameter) {
        return null;
    }

    /**
     * 判断是否会与当前用户交互
     * @param user 当前交互人
     * @return 是否会与其交互
     */
    default boolean willInteract(XiaomingUser user) {
        return false;
    }

    /**
     * 交互方法的细节
     * @author Chuanwise
     */
    @Data
    @AllArgsConstructor
    class InteractorMethodDetail {
        private Method method;
        private String[] requiredPermissions;

        public InteractorMethodDetail(@NotNull final Method method) {
            this.method = method;

            final List<String> requiredPermissions = new ArrayList<>();
            for (RequirePermission requiredPermission : method.getAnnotationsByType(RequirePermission.class)) {
                requiredPermissions.add(requiredPermission.value());
            }
            this.requiredPermissions = requiredPermissions.toArray(new String[0]);
        }
    }
}
