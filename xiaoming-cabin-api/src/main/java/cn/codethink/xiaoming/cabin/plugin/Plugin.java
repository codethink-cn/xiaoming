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

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cause.Cause;

/**
 * <h1>Plugin</h1>
 *
 * <p>Plugin is a set of functions NOT related to a bot directly
 * and strongly. It always be high-level, such as permission service. </p>
 *
 * @author Chuanwise
 */
public interface Plugin
    extends Subject {

    /**
     * Get state.
     *
     * @return state
     */
    PluginState getState();

    /**
     * Load plugin.
     *
     * @param cause cause
     * @throws Exception exception thrown in loading plugin.
     */
    void load(Cause cause) throws Exception;
}
