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


import cn.codethink.xiaoming.state.StateType;

/**
 * <h1>Plugin State Type</h1>
 *
 * @author Chuanwise
 */
public enum PluginStateType
        implements StateType {

    /**
     * Ready for loading, but never been started after constructed.
     */
    READY,

    /**
     * Loading.
     */
    LOADING,

    /**
     * Error occurred in loading plugin.
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
     * Error occurred in enabling plugin.
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
     * Error occurred in disabling plugin.
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
     * Error occurred in unloading plugin.
     */
    UNLOADING_ERROR,

    /**
     * Unloaded.
     */
    UNLOADED
}