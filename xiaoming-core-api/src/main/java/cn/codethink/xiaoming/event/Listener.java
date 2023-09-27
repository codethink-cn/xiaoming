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
import cn.codethink.xiaoming.api.BotApiFactory;

import java.util.Set;

/**
 * <h1>Listener</h1>
 *
 * <p>Listener is a function will be called when corresponding events
 * are published. </p>
 *
 * @param <T> event class
 * @author Chuanwise
 */
public interface Listener<T> {

    interface Builder<T> {

        @SuppressWarnings("all")
        <U> Builder<U> eventClasses(Class<? extends U>... eventClasses);

        <U> Builder<U> eventClass(Class<U> eventClass);

        Builder<T> ignoreCancelledEvent(boolean ignoreCancelledEvent);

        Builder<T> order(Order order);

        Builder<T> action(ListenerAction<T> action);

        Builder<T> subject(Subject subject);

        Listener<T> build();
    }

    /**
     * Get a listener builder.
     *
     * @return listener builder
     */
    static Builder<?> builder() {
        return BotApiFactory.getBotApi().getListenerBuilder();
    }

    /**
     * Get event classes.
     *
     * @return event classes
     */
    Set<Class<? extends T>> getEventClasses();

    /**
     * Handle event.
     *
     * @param context event handling context
     * @throws Exception exception thrown in handling event
     */
    void listen(EventListeningContext<T> context) throws Exception;

    /**
     * Query if cancelled events are ignored by the listener.
     *
     * @return If cancelled events are ignored by the listener.
     */
    boolean isIgnoreCancelledEvent();

    /**
     * Get subject.
     *
     * @return subject
     */
    Subject getSubject();

    /**
     * Get order.
     *
     * @return order
     */
    Order getOrder();
}
