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

package cn.codethink.xiaoming.api;

import cn.codethink.xiaoming.annotation.BotInternalApi;
import cn.codethink.xiaoming.event.Listener;
import cn.codethink.xiaoming.id.LongId;
import cn.codethink.xiaoming.id.StringId;
import cn.codethink.xiaoming.time.Time;

/**
 * <h1>Bot Api</h1>
 *
 * <p>Bot api is the bridge between xiaoming bot api and core. </p>
 *
 * @author Chuanwise
 */
@BotInternalApi
public interface BotApi {

    LongId getLongId(long value);
    StringId getStringId(String value);

    Listener.Builder<?> getListenerBuilder();

    Time getTimeOfNow();
    Time getTimeOfSeconds(long seconds);
    Time getTimeOfMilliseconds(long milliseconds);
}
