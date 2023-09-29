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

package cn.codethink.xiaoming.cause;

import cn.codethink.xiaoming.time.Time;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class ExceptionCauseImpl
    extends AbstractCause
    implements ExceptionCause {

    private final Throwable exception;
    private final Time time;
    private final boolean retryable;
    private String toStringCache;
    private Integer hashCodeCache;

    public ExceptionCauseImpl(Throwable exception, Time time, boolean retryable) {
        Preconditions.checkNotNull(exception, "Exception is null! ");
        Preconditions.checkNotNull(time, "Time is null! ");

        this.exception = exception;
        this.time = time;
        this.retryable = retryable;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean isRetryable() {
        return retryable;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExceptionCause)) {
            return false;
        }
        final ExceptionCause exceptionCause = (ExceptionCause) obj;
        return exception.equals(exceptionCause.getException())
                && time.equals(exceptionCause.getTime());
    }

    @Override
    public int hashCode() {
        if (hashCodeCache == null) {
            hashCodeCache = Objects.hash(exception, time);
        }
        return hashCodeCache;
    }

    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = "ExceptionCause(exception='" + exception + "', time='" + time + "')";
        }
        return toStringCache;
    }

    @Override
    public String getDescription() {
        return "Exception thrown: '" + exception + "'";
    }
}
