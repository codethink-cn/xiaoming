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

/**
 * <h1>Abstract Cancellable Event</h1>
 *
 * <p>Abstract cancellable event is a simple implementation of
 * {@link CancellableEvent} for developers to inherit from. </p>
 *
 * <p>Not all cancellable events must be inherited from this class,
 * but if you are developing a simple event, it's recommended to
 * inherit from it. </p>
 *
 * @see Event
 * @see CancellableEvent
 * @author Chuanwise
 */
public abstract class AbstractCancellableEvent
    extends AbstractEvent
    implements CancellableEvent {

    /**
     * field to record if event is cancelled.
     */
    private volatile boolean cancelled = false;

    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
