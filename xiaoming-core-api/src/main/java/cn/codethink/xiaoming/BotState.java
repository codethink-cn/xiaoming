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
import cn.codethink.xiaoming.time.Time;

/**
 * <h1>Bot State</h1>
 *
 * @author Chuanwise
 */
public interface BotState {

    enum Type {

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
        STARTING_ERROR(false, true),

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
        STOPPING_ERROR(false, true),

        /**
         * Exception thrown in starting.
         */
        STOPPED(false, false);

        private final boolean doing;
        private final boolean error;

        Type(boolean doing, boolean error) {
            this.doing = doing;
            this.error = error;
        }

        public boolean isDoing() {
            return doing;
        }

        public boolean isError() {
            return error;
        }
    }

    /**
     * Get type.
     *
     * @return type
     */
    Type getType();

    /**
     * Get cause.
     *
     * @return cause
     */
    Cause getCause();
}
