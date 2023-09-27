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

import cn.codethink.xiaoming.event.FunctionalListener;
import cn.codethink.xiaoming.event.Listener;
import cn.codethink.xiaoming.id.LongId;
import cn.codethink.xiaoming.id.LongIdImpl;
import cn.codethink.xiaoming.id.StringId;
import cn.codethink.xiaoming.id.StringIdImpl;
import cn.codethink.xiaoming.time.MillisecondTime;
import cn.codethink.xiaoming.time.SecondTime;
import cn.codethink.xiaoming.time.Time;

public class BotApiImpl
    implements BotApi {

    @Override
    public LongId getLongId(long value) {
        return new LongIdImpl(value);
    }

    @Override
    public StringId getStringId(String value) {
        return new StringIdImpl(value);
    }

    @Override
    public Listener.Builder<?> getListenerBuilder() {
        return new FunctionalListener.BuilderImpl<>();
    }

    @Override
    public Time getTimeOfNow() {
        return new MillisecondTime(System.currentTimeMillis());
    }

    @Override
    public Time getTimeOfSeconds(long seconds) {
        return new SecondTime(seconds);
    }

    @Override
    public Time getTimeOfMilliseconds(long milliseconds) {
        return new MillisecondTime(milliseconds);
    }
}
