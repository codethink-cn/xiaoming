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

/**
 * <h1>Listener Action</h1>
 *
 * <p>Listener action is an functional interface accepting {@link EventListeningContext}
 * and allowed to throw checked exception. </p>
 *
 * @param <T> event class
 * @author Chuanwise
 */
@FunctionalInterface
public interface ListenerAction<T> {

    /**
     * Handle event.
     *
     * @param context context
     * @throws Exception exception thrown in handling event
     */
    void listen(EventListeningContext<T> context) throws Exception;
}
