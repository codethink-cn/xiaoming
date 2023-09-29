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

package cn.codethink.xiaoming.cabin.plugin;

import cn.codethink.xiaoming.BotState;
import cn.codethink.xiaoming.cause.Cause;

/**
 * <h1>Plugin State</h1>
 *
 * <p>Plugin state represents different states of plugins. </p>
 *
 * @author Chuanwise
 */
public interface PluginState {

    /**
     * Type of bot state.
     */
    enum Type {

        /**
         * Ready for loading, but never been started after constructed.
         */
        READY(false, false),

        /**
         * Loading.
         */
        LOADING(false, false),

        /**
         * Error occurred in loading plugin.
         */
        LOADING_ERROR(false, false),

        /**
         * Loaded.
         */
        LOADED(false, false),

        /**
         * Enabling.
         */
        ENABLING(false, false),

        /**
         * Error occurred in enabling plugin.
         */
        ENABLING_ERROR(false, false),

        /**
         * Enabled.
         */
        ENABLED(false, false),

        /**
         * Disabling.
         */
        DISABLING(false, false),

        /**
         * Error occurred in disabling plugin.
         */
        DISABLING_ERROR(false, false),

        /**
         * Disabled.
         */
        DISABLED(false, false),

        /**
         * Unloading.
         */
        UNLOADING(false, false),

        /**
         * Error occurred in unloading plugin.
         */
        UNLOADING_ERROR(false, false),

        /**
         * Unloaded.
         */
        UNLOADED(false, false);

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
    BotState.Type getType();

    /**
     * Get cause.
     *
     * @return cause
     */
    Cause getCause();
}
