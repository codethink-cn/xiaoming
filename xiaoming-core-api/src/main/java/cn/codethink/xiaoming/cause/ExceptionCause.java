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

import cn.codethink.xiaoming.api.BotApiFactory;
import cn.codethink.xiaoming.time.Time;

/**
 * <h1>Exception Cause</h1>
 *
 * <p>Exception cause represents an exception. </p>
 *
 * @author Chuanwise
 */
public interface ExceptionCause
        extends Cause {

    /**
     * Construct an exception cause with provided exception.
     *
     * @param exception exception
     * @param retryable retryable
     * @return exception cause
     * @throws NullPointerException exception is null
     */
    static ExceptionCause of(Throwable exception, boolean retryable) {
        return BotApiFactory.getBotApi().getExceptionCause(exception, Time.now(), retryable);
    }

    /**
     * Construct an exception cause with provided exception and time.
     *
     * @param exception exception
     * @param retryable retryable
     * @return exception cause
     * @throws NullPointerException exception is null
     */
    static ExceptionCause of(Throwable exception, Time time, boolean retryable) {
        return BotApiFactory.getBotApi().getExceptionCause(exception, time, retryable);
    }

    /**
     * Get exception.
     *
     * @return exception
     */
    Throwable getException();
}
