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

import cn.codethink.xiaoming.cause.Cause;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class StateImpl<T extends StateType>
        implements State<T> {

    private final T stateType;
    private final Cause cause;
    private String toStringCache;
    private Integer hashCodeCache;

    public StateImpl(T stateType, Cause cause) {
        Preconditions.checkNotNull(stateType, "State type is null! ");
        Preconditions.checkNotNull(cause, "Cause is null! ");

        this.stateType = stateType;
        this.cause = cause;
    }

    @Override
    public T getType() {
        return stateType;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    @Override
    public int hashCode() {
        if (hashCodeCache == null) {
            hashCodeCache = Objects.hash(stateType, cause);
        }
        return hashCodeCache;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) {
            return false;
        }
        final State<?> state = (State<?>) obj;
        return stateType.equals(state.getType())
                && cause.equals(state.getCause());
    }

    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = "State(type=" + stateType + ", cause='" + cause.getDescription() + "')";
        }
        return toStringCache;
    }
}
