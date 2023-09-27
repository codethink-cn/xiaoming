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

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cause.Cause;

/**
 * <h1>Event Listening Context</h1>
 *
 * <p>Event listening context provided information about the publishing
 * operation, including the instance of bot, cause, logger, etc. </p>
 *
 * @param <T> event class
 * @author Chuanwise
 */
public interface EventListeningContext<T> {

    /**
     * Get publisher.
     *
     * @return publisher
     */
    Subject getPublisher();

    /**
     * Get event.
     *
     * @return event
     */
    T getEvent();

    /**
     * Get bot.
     *
     * @return bot
     */
    Bot getBot();
}
