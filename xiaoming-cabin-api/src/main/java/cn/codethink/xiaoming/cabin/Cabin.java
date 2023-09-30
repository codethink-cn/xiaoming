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

package cn.codethink.xiaoming.cabin;

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cabin.configuration.CabinConfiguration;
import cn.codethink.xiaoming.cabin.event.EventManager;
import cn.codethink.xiaoming.cause.Cause;
import cn.codethink.xiaoming.state.StateHolder;

/**
 * <h1>Cabin</h1>
 *
 * <p>Cabin is a plugin framework based on xiaoming core. </p>
 *
 * @author Chuanwise
 */
public interface Cabin
        extends Subject, StateHolder<CabinStateType> {

    /**
     * Get configuration.
     *
     * @return configuration
     */
    CabinConfiguration getConfiguration();

    /**
     * Get event manager.
     *
     * @return event manager
     */
    EventManager getEventManager();

    /**
     * Start cabin.
     *
     * @param cause   cause
     * @param subject subject
     * @throws Exception exception thrown in starting cabin.
     */
    void start(Cause cause, Subject subject) throws Exception;

    /**
     * Stop cabin.
     *
     * @param cause   cabin
     * @param subject subject
     * @throws Exception exception thrown in stopping cabin.
     */
    void stop(Cause cause, Subject subject) throws Exception;
}
