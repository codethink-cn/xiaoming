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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FunctionalListener<T>
    extends AbstractListener<T> {

    public static class BuilderImpl<T>
        implements Builder<T> {

        private Set<Class<? extends T>> eventClasses = Collections.emptySet();
        private boolean ignoreCancelledEvent;
        private Order order;
        private ListenerAction<T> action;
        private Subject subject;

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public <U> Builder<U> eventClasses(Class<? extends U>... eventClasses) {
            this.eventClasses = (Set) new HashSet<>(Arrays.asList(eventClasses));
            return (Builder<U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Builder<U> eventClass(Class<U> eventClass) {
            this.eventClasses = Collections.singleton((Class<? extends T>) eventClass);
            return (Builder<U>) this;
        }

        @Override
        public Builder<T> ignoreCancelledEvent(boolean ignoreCancelledEvent) {
            this.ignoreCancelledEvent = ignoreCancelledEvent;
            return this;
        }

        @Override
        public Builder<T> order(Order order) {
            this.order = order;
            return this;
        }

        @Override
        public Builder<T> action(ListenerAction<T> action) {
            this.action = action;
            return this;
        }

        @Override
        public Builder<T> subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        @Override
        public Listener<T> build() {
            return new FunctionalListener<>(eventClasses, order, ignoreCancelledEvent, subject, action);
        }
    }
    private final ListenerAction<T> action;

    public FunctionalListener(Set<Class<? extends T>> eventClasses, Order order, boolean ignoreCancelledEvent, Subject subject,
                              ListenerAction<T> action) {
        super(eventClasses, order, ignoreCancelledEvent, subject);

        Preconditions.checkNotNull(action, "Action is null!");

        this.action = action;
    }

    @Override
    public void listen(EventListeningContext<T> context) throws Exception {
        Preconditions.checkNotNull(context, "Event listening context is null!");

        action.listen(context);
    }
}
