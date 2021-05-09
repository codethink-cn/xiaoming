package com.chuanwise.xiaoming.core.interactor;

import com.chuanwise.xiaoming.api.annotation.InteractMethod;
import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.event.UserInteractor;
import com.chuanwise.xiaoming.api.exception.InteactorTimeoutException;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 交互器标准实现
 * @author Chuanwise
 */
@EqualsAndHashCode
public class InteractorImpl extends XiaomingObjectImpl implements Interactor {
    /**
     * 交互方法记录器
     */
    private final Set<InteractorMethodDetail> interactorMethodDetails = new HashSet<>();

    @Override
    public void reloadInteractorDetails(Logger logger) {
        interactorMethodDetails.clear();
        final Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (method.getAnnotationsByType(InteractMethod.class).length != 0) {
                interactorMethodDetails.add(new InteractorMethodDetail(method));
            }
        }
        if (interactorMethodDetails.isEmpty()) {
            logger.warn("没有从加载任何子交互方法");
        } else {
            logger.info("成功加载了 {} 个子交互方法", interactorMethodDetails.size());
        }
    }

    @Override
    public final boolean interact(XiaomingUser user) throws Exception {
        for (InteractorMethodDetail detail : interactorMethodDetails) {
            final Method method = detail.getMethod();
            // 检查有无权限
            if (!user.hasPermissions(detail.getRequiredPermissions())) {
                continue;
            }

            // 填充参数
            final List<Object> arguments = new ArrayList<>();
            final Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                final Class<?> type = parameter.getType();

                if (GroupXiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof GroupXiaomingUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else if (PrivateXiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof PrivateXiaomingUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else if (XiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof XiaomingUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else {
                    final Object o = onParameter(user, parameter);
                    if (Objects.nonNull(o)) {
                        arguments.add(o);
                    } else {
                        throw new NoSuchElementException("parameters of interact method must all be instance of InteractorUser");
                    }
                }
            }

            // 参数不一致说明填充失败，继续寻找方法
            if (arguments.size() != parameters.length) {
                continue;
            }

            try {
                method.invoke(this, arguments.toArray(new Object[0]));
            } catch (InvocationTargetException invocationTargetException) {
                // 抽取异常触发原因。如果是 Timeout 直接处理掉，否则继续抛出
                final Throwable cause = invocationTargetException.getCause();
                if (cause instanceof InteactorTimeoutException) {
                    final UserInteractRunnable runnable = user.getUserInteractRunnable();
                    runnable.setMessageWaiter(null);
                    runnable.setInteractor(null);
                } else if (Objects.nonNull(cause)) {
                    throw (Exception) cause;
                } else {
                    invocationTargetException.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }
}