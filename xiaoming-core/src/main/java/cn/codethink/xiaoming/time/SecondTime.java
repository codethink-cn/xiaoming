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

package cn.codethink.xiaoming.time;

import com.google.common.base.Preconditions;

import java.util.concurrent.TimeUnit;

public class SecondTime
    extends AbstractTime {

    private final long seconds;

    public SecondTime(long seconds) {
        Preconditions.checkArgument(seconds >= 0, "Seconds must be greater than 0, but got " + seconds + "!");

        this.seconds = seconds;
    }

    @Override
    public long toSeconds() {
        return seconds;
    }

    @Override
    public long toMilliseconds() {
        return TimeUnit.SECONDS.toMillis(seconds);
    }
}
