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

import cn.codethink.xiaoming.OperationCancelledException;
import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cause.AbstractCause;
import cn.codethink.xiaoming.cause.Cause;
import cn.codethink.xiaoming.time.Time;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private final ReentrantReadWriteLock interceptionStateLock = new ReentrantReadWriteLock();

    private final List<InterceptionStateOperation> interceptionStateOperations = new ArrayList<>();

    private final List<InterceptionStateOperation> unmodifiableInterceptionStateOperations =
            Collections.unmodifiableList(interceptionStateOperations);

    private boolean intercepted = false;
    private String descriptionCache;

    public AbstractEvent(Cause cause, Time time) {
        super(cause, time);
    }

    public AbstractEvent(Cause cause) {
        super(cause);
    }

    public AbstractEvent() {
    }

    @Override
    public final boolean isIntercepted() {
        try {
            interceptionStateLock.readLock().lockInterruptibly();
        } catch (InterruptedException e) {
            throw new OperationCancelledException("Acquiring the read lock of interception state is interrupted! ", e);
        }
        try {
            return intercepted;
        } finally {
            interceptionStateLock.readLock().unlock();
        }
    }

    @Override
    public InterceptionStateOperation setIntercepted(boolean intercepted, Cause cause, Subject subject) {
        Preconditions.checkNotNull(cause, "Cause is null! ");
        Preconditions.checkNotNull(subject, "Subject is null! ");

        final InterceptionStateOperation operation = InterceptionStateOperation.of(intercepted, this, cause, subject);

        try {
            interceptionStateLock.writeLock().lockInterruptibly();
        } catch (final InterruptedException e) {
            throw new OperationCancelledException("Acquiring the write lock of interception state is interrupted! ", e);
        }
        try {
            interceptionStateOperations.add(operation);
            this.intercepted = intercepted;
            return operation;
        } finally {
            interceptionStateLock.writeLock().unlock();
        }
    }

    @Override
    public List<InterceptionStateOperation> getInterceptionStateOperations() {
        return unmodifiableInterceptionStateOperations;
    }

    @Override
    public String getDescription() {
        if (descriptionCache == null) {
            descriptionCache = "Event '" + getClass().getSimpleName() + "'";
        }
        return descriptionCache;
    }
}
