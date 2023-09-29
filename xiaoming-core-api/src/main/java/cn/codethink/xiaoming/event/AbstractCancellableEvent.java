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
import cn.codethink.xiaoming.cause.Cause;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private final ReentrantReadWriteLock cancellationStateLock = new ReentrantReadWriteLock();

    private final List<CancellationStateOperation> cancellationStateOperations = new CopyOnWriteArrayList<>();

    private final List<CancellationStateOperation> unmodifiableCancellationStateOperations =
            Collections.unmodifiableList(cancellationStateOperations);

    private boolean cancelled;

    private volatile Cause cancellationCause;

    public AbstractCancellableEvent(Cause cause) {
        super(cause);
    }

    public AbstractCancellableEvent() {
    }

    @Override
    public boolean isCancelled() {
        try {
            cancellationStateLock.readLock().lockInterruptibly();
        } catch (InterruptedException e) {
            throw new OperationCancelledException("Acquiring the read lock of cancellation state is interrupted! ", e);
        }
        try {
            return cancelled;
        } finally {
            cancellationStateLock.readLock().unlock();
        }
    }

    @Override
    public CancellationStateOperation setCancelled(boolean cancelled, Cause cause, Subject subject) {
        Preconditions.checkNotNull(cause, "Cause is null! ");
        Preconditions.checkNotNull(subject, "Subject is null! ");

        final CancellationStateOperation operation = CancellationStateOperation.of(cancelled, this, cause, subject);

        try {
            cancellationStateLock.writeLock().lockInterruptibly();
        } catch (final InterruptedException e) {
            throw new OperationCancelledException("Acquiring the write lock of cancellation state is interrupted! ", e);
        }
        try {
            cancellationStateOperations.add(operation);
            this.cancelled = cancelled;
            return operation;
        } finally {
            cancellationStateLock.writeLock().unlock();
        }

    }

    @Override
    public List<CancellationStateOperation> getCancellationStateOperations() {
        return unmodifiableCancellationStateOperations;
    }
}
