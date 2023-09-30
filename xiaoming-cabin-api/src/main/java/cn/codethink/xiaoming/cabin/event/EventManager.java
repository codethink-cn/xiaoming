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

package cn.codethink.xiaoming.cabin.event;

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.event.Event;
import cn.codethink.xiaoming.event.Listener;
import cn.codethink.xiaoming.event.Listeners;

/**
 * <h1>Event Manager</h1>
 *
 * <p>Event manager manages all objects related to events,
 * such as listeners. </p>
 *
 * @author Chuanwise
 */
public interface EventManager {

    /**
     * Register listeners in provided class.
     *
     * @param subject   subject
     * @param listeners listeners
     * @throws IllegalArgumentException illegal listeners present
     * @throws NullPointerException     listeners or subject is null
     */
    void registerListeners(Subject subject, Listeners listeners);

    /**
     * Register listeners in provided class.
     *
     * @param subject   subject
     * @param listeners listeners
     * @throws IllegalArgumentException illegal listeners present
     * @throws NullPointerException     listeners or subject is null
     */
    void registerListeners(Subject subject, Listeners... listeners);

    /**
     * Register listener.
     *
     * @param listener listener
     * @throws NullPointerException listeners is null
     */
    void registerListener(Listener<?> listener);

    /**
     * Publish an event.
     *
     * @param event     event
     * @param publisher publisher
     * @throws NullPointerException event or publisher is null
     */
    void publishEvent(Event event, Subject publisher);
}
