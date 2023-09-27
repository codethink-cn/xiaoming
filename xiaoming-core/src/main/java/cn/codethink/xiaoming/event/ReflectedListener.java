/*
 * Copyright 2023 CodeThink Technologies and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Subject;
import com.google.common.base.Preconditions;

import java.lang.reflect.Method;
import java.util.Set;

public class ReflectedListener
    extends AbstractListener<Event> {

    private static final Object[] EMPTY_ARGUMENT_ARRAY = {};
    private final Listeners object;
    private final Method method;
    private final Class<?> parameterType;

    @SuppressWarnings("unchecked")
    public ReflectedListener(Set<Class<?>> eventClasses, Order order, boolean ignoreCancelledEvent, Subject subject,
                             Listeners listeners, Method method, Class<?> parameterType) {
        super((Set<Class<? extends Event>>) eventClasses, order, ignoreCancelledEvent, subject);

        Preconditions.checkNotNull(listeners, "Listeners are null!");
        Preconditions.checkNotNull(method, "Method is null!");

        this.object = listeners;
        this.method = method;
        this.parameterType = parameterType;
    }

    @Override
    public void listen(EventListeningContext<Event> context) throws Exception {
        Preconditions.checkNotNull(context, "Event listening context is null!");

        final Object[] arguments;
        if (parameterType == null) {
            arguments = EMPTY_ARGUMENT_ARRAY;
        } else {
            arguments = new Object[1];
            arguments[0] = context.getEvent();
        }

        method.invoke(object, arguments);
    }
}
