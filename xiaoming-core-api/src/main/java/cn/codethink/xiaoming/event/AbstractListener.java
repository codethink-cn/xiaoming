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

import java.util.Set;

/**
 * <h1>Abstract Listener</h1>
 *
 * <p>Abstract listener is a simple implementation of {@link Listener} for
 * developers to inherit from. </p>
 *
 * <p>Not all listeners must be inherited from this class, but if you are
 * developing a simple listener, it's recommended to inherit from it. </p>
 *
 * @param <T> event class
 * @see Listener
 * @author Chuanwise
 */
public abstract class AbstractListener<T>
    implements Listener<T> {

    private final Set<Class<? extends T>> eventClasses;
    private final Order order;
    private final boolean ignoreCancelledEvent;
    private final Subject subject;

    public AbstractListener(Set<Class<? extends T>> eventClasses, Order order, boolean ignoreCancelledEvent, Subject subject) {
        Preconditions.checkNotNull(eventClasses, "Event classes are null! ");
        Preconditions.checkArgument(!eventClasses.isEmpty(), "Event classes are empty!");
        Preconditions.checkArgument(!eventClasses.contains(null), "Event classes contains null!");
        Preconditions.checkNotNull(order, "Order is null! ");
        Preconditions.checkNotNull(subject, "Subject is null! ");

        this.eventClasses = eventClasses;
        this.order = order;
        this.ignoreCancelledEvent = ignoreCancelledEvent;
        this.subject = subject;
    }

    @Override
    public final Set<Class<? extends T>> getEventClasses() {
        return eventClasses;
    }

    @Override
    public final boolean isIgnoreCancelledEvent() {
        return ignoreCancelledEvent;
    }

    @Override
    public final Subject getSubject() {
        return subject;
    }

    @Override
    public final Order getOrder() {
        return order;
    }
}
