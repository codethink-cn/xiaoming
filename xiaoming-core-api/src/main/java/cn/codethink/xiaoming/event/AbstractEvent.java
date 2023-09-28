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

import cn.codethink.xiaoming.cause.AbstractCause;
import cn.codethink.xiaoming.cause.Cause;
import cn.codethink.xiaoming.time.Time;
import com.google.common.base.Preconditions;

/**
 * <h1>Abstract Event</h1>
 *
 * <p>Abstract event is a simple implementation of {@link Event} for
 * developers to inherit from. </p>
 *
 * <p>Not all events must be inherited from this class, but if you are
 * developing a simple event, it's recommended to inherit from it. </p>
 *
 * @see Event
 * @author Chuanwise
 */
public abstract class AbstractEvent
    extends AbstractCause
    implements Event {

    /**
     * Field to record if event is intercepted.
     */
    private volatile boolean intercepted = false;
    private String descriptionCache;

    public AbstractEvent(Cause cause, Time time) {
        super(cause, time);
    }

    public AbstractEvent(Cause cause) {
        super(cause);
    }

    public AbstractEvent() {
    }

    public final boolean isIntercepted() {
        return intercepted;
    }

    public final void setIntercepted(boolean intercepted) {
        this.intercepted = intercepted;
    }

    @Override
    public String getDescription() {
        if (descriptionCache == null) {
            descriptionCache = "Event '" + getClass().getSimpleName() + "'";
        }
        return descriptionCache;
    }
}
