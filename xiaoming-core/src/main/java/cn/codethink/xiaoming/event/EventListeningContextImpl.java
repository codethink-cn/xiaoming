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
import com.google.common.base.Preconditions;

public class EventListeningContextImpl
    implements EventListeningContext {

    private final Event event;
    private final Subject publisher;
    private final Bot bot;

    public EventListeningContextImpl(Event event, Subject publisher, Bot bot) {
        Preconditions.checkNotNull(event, "Event is null!");
        Preconditions.checkNotNull(publisher, "Publisher is null!");
        Preconditions.checkNotNull(bot, "Bot is null!");

        this.event = event;
        this.publisher = publisher;
        this.bot = bot;
    }

    @Override
    public Subject getPublisher() {
        return publisher;
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public Bot getBot() {
        return bot;
    }
}
