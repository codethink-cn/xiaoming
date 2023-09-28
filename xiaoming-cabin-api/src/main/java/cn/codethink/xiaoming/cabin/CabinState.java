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

package cn.codethink.xiaoming.cabin;

import cn.codethink.xiaoming.cause.Cause;

/**
 * <h1>Cabin State</h1>
 *
 * @author Chuanwise
 */
public interface CabinState {

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
