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

package cn.codethink.xiaoming.state;

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.event.Event;

/**
 * <h1>State Switching Event</h1>
 *
 * <p>State switching event is event will be published when
 * the state of something will be changed. </p>
 *
 * @param <T> state type
 * @author Chuanwise
 */
public interface StateSwitchingEvent<T extends StateType>
        extends Event {

    /**
     * Get next state type.
     *
     * @return next state type
     */
    T getNextStateType();

    /**
     * Get holder.
     *
     * @return holder
     */
    StateHolder<T> getHolder();

    /**
     * Get switcher.
     *
     * @return switcher
     */
    Subject getSwitcher();
}
