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

/**
 * <h1>Plugin State</h1>
 *
 * <p>Plugin state represents different states of plugins. </p>
 *
 * @author Chuanwise
 */
public enum PluginState {

    /**
     * Unloaded.
     */
    UNLOADED,

    /**
     * Loading.
     */
    LOADING,

    /**
     * Exception thrown in loading.
     */
    LOADING_ERROR,

    /**
     * Loaded.
     */
    LOADED,

    /**
     * Enabling.
     */
    ENABLING,

    /**
     * Exception thrown in enabling.
     */
    ENABLING_ERROR,

    /**
     * Enabled.
     */
    ENABLED,

    /**
     * Disabling.
     */
    DISABLING,

    /**
     * Exception thrown in disabling.
     */
    DISABLING_ERROR,

    /**
     * Disabled.
     */
    DISABLED,

    /**
     * Unloading.
     */
    UNLOADING,

    /**
     * Exception thrown in unloading.
     */
    UNLOADING_ERROR,
}
