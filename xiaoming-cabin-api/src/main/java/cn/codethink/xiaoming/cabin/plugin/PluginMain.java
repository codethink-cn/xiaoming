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
 * <h1>Plugin Main</h1>
 *
 * <p>Plugin main is the main class for a plugin. Every plugin developer is
 * required to provide a plugin main class implemented this interface
 * directly or indirectly. </p>
 *
 * @author Chuanwise
 */
public interface PluginMain {

    /**
     * Load plugin.
     *
     * @param context context
     * @throws Exception exception thrown in loading plugin.
     */
    default void load(PluginLoadingContext context) throws Exception {
    }

    /**
     * Enable plugin.
     *
     * @param context context
     * @throws Exception exception thrown in enabling plugin.
     */
    default void enable(PluginEnablingContext context) throws Exception {
    }

    /**
     * Disable plugin.
     *
     * @param context context
     * @throws Exception exception thrown in disabling plugin.
     */
    default void disable(PluginDisablingContext context) throws Exception {
    }
}
