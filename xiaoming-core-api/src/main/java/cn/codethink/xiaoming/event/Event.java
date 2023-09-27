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

import cn.codethink.xiaoming.cause.Cause;

/**
 * <h1>Event</h1>
 *
 * <p>Just like many similar implementations, events are something matter,
 * which will be published when special things happens. </p>
 *
 * <p>Event can be intercepted by {@code event.setIntercepted(true)}. After
 * intercepted, event will not be spread to other event listeners. </p>
 *
 * @author Chuanwise
 */
public interface Event
    extends Cause {

    /**
     * Query if event is intercepted.
     *
     * @return if event is intercepted
     */
    boolean isIntercepted();

    /**
     * Set if event is intercepted.
     *
     * @param intercepted if event is intercepted
     */
    void setIntercepted(boolean intercepted);
}
