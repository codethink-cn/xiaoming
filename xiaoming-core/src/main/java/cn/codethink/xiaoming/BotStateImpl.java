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

package cn.codethink.xiaoming;

import cn.codethink.xiaoming.cause.Cause;
import com.google.common.base.Preconditions;

public class BotStateImpl
    implements BotState {

    private final Type type;
    private final Cause cause;

    public BotStateImpl(Type type, Cause cause) {
        Preconditions.checkNotNull(type, "Type is null!");
        Preconditions.checkNotNull(cause, "Cause is null!");

        this.type = type;
        this.cause = cause;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
