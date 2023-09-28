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

import java.util.List;
import java.util.NoSuchElementException;

/**
 * <h1>Cause</h1>
 *
 * <p>Cause is the reason for an operation or state changing. </p>
 *
 * @author Chuanwise
 */
public interface Cause {

    /**
     * Construct a cause with provided description.
     *
     * @param description description
     * @return cause
     * @throws NullPointerException     description is null
     * @throws IllegalArgumentException description is empty
     */
    static Cause of(String description) {
        return BotApiFactory.getBotApi().getDescriptionCause(description);
    }

    /**
     * Get description.
     *
     * @return description
     */
    String getDescription();

    /**
     * Get time.
     *
     * @return time
     */
    Time getTime();

    /**
     * Get direct cause.
     *
     * @return cause or null
     */
    Cause getDirectCause();

    /**
     * Get direct cause.
     *
     * @return cause
     * @throws NoSuchElementException cause isn't present
     */
    Cause getDirectCauseOrFail();

    /**
     * Get causes.
     *
     * @return causes
     */
    List<Cause> getCauses();

    /**
     * Get cause with provided class.
     *
     * @param causeClass cause class
     * @param <T>        cause class
     * @return cause or null
     * @throws NullPointerException cause class is null
     */
    <T extends Cause> T getCause(Class<T> causeClass);

    /**
     * Get cause with provided class.
     *
     * @param causeClass cause class
     * @param <T>        cause class
     * @return cause
     * @throws NullPointerException   cause class is null
     * @throws NoSuchElementException cause with provided class isn't present
     */
    <T extends Cause> T getCauseOrFail(Class<T> causeClass);
}
