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
import cn.codethink.xiaoming.cause.AbstractCause;
import cn.codethink.xiaoming.cause.Cause;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class InterceptionStateOperationImpl
        extends AbstractCause
        implements InterceptionStateOperation {

    private final Subject subject;
    private final boolean intercepted;
    private final Event event;
    private Integer hashCodeCache;

    public InterceptionStateOperationImpl(boolean intercepted, Event event, Cause cause, Subject subject) {
        super(cause);

        Preconditions.checkNotNull(subject, "Subject is null! ");
        Preconditions.checkNotNull(event, "Event is null! ");

        this.event = event;
        this.intercepted = intercepted;
        this.subject = subject;
    }

    @Override
    public String getDescription() {
        return "Event cancelled by '" + subject + "'";
    }

    @Override
    public boolean isIntercepted() {
        return intercepted;
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public Subject getSubject() {
        return subject;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InterceptionStateOperation)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final InterceptionStateOperation operation = (InterceptionStateOperation) obj;
        return event.equals(operation.getEvent())
                && subject.equals(operation.getSubject());
    }

    @Override
    public int hashCode() {
        if (hashCodeCache == null) {
            hashCodeCache = Objects.hash(super.hashCode(), event, subject, intercepted);
        }
        return hashCodeCache;
    }
}
