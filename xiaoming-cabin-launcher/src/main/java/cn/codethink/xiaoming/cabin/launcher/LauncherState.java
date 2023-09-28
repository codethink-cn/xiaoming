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

package cn.codethink.xiaoming.cabin.launcher;

import cn.codethink.xiaoming.cause.Cause;

import java.util.Objects;

/**
 * <h1>Launcher State</h1>
 *
 * @author Chuanwise
 */
public final class LauncherState {
    public enum Type {

        /**
         * Ready for starting, but never been started after constructed.
         */
        READY(false, false),

        /**
         * Starting.
         */
        STARTING(true, false),

        /**
         * Exception thrown in starting. Use {@link #getCause()} to get more details.
         */
        STARTING_FAILED(false, true),

        /**
         * Started.
         */
        STARTED(false, false),

        /**
         * Stopping.
         */
        STOPPING(true, false),

        /**
         * Exception thrown in stopping. Use {@link #getCause()} to get more details.
         */
        STOPPING_FAILED(false, true),

        /**
         * Exception thrown in starting.
         */
        STOPPED(false, false);

        private final boolean doing;
        private final boolean failed;

        Type(boolean doing, boolean failed) {
            this.doing = doing;
            this.failed = failed;
        }

        public boolean isDoing() {
            return doing;
        }

        public boolean isFailed() {
            return failed;
        }
    }

    private final Type type;
    private final Cause cause;
    private Integer hashCodeCache;

    public LauncherState(Type type, Cause cause) {
        this.type = type;
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LauncherState)) {
            return false;
        }
        final LauncherState launcherState = (LauncherState) obj;
        return cause.equals(launcherState.cause) && type == launcherState.type;
    }

    @Override
    public String toString() {
        return "LauncherState(type=" + type + ", cause='" + cause.getDescription() + "')";
    }

    @Override
    public int hashCode() {
        if (hashCodeCache == null) {
            hashCodeCache = Objects.hash(type, cause);
        }
        return hashCodeCache;
    }
}
