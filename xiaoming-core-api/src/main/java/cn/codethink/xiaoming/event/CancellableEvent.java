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
import cn.codethink.xiaoming.cause.Cause;

import java.util.List;

/**
 * <h1>Cancellable Event</h1>
 *
 * <p>Cancellable event represents a cancellable operation. After published,
 * the corresponding operation will be cancelled if {@link #isCancelled()}
 * returns {@code true}. </p>
 *
 * @author Chuanwise
 */
public interface CancellableEvent
    extends Event {

    /**
     * Get cancellation state operations.
     *
     * @return cancellation state operation
     */
    List<CancellationStateOperation> getCancellationStateOperations();

    /**
     * Query if event is cancelled.
     *
     * @return if event is cancelled
     */
    boolean isCancelled();

    /**
     * Set cancellation state of event.
     *
     * @param cancelled cancelled
     * @param cause     cause
     * @param subject   canceller
     * @return cancellation state operation
     * @throws NullPointerException cause or subject is null
     */
    CancellationStateOperation setCancelled(boolean cancelled, Cause cause, Subject subject);
}
