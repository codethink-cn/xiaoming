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
import java.util.function.Consumer;

public class FunctionalListener<T>
    extends AbstractListener<T> {
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
