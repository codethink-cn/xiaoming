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

import cn.codethink.xiaoming.cabin.Cabin;
import cn.codethink.xiaoming.cause.Cause;

/**
 * <h1>Plugin Loading Context</h1>
 *
 * <p>Plugin loading context provided information about the loading
 * operation, including the instance of cabin, cause, logger, etc. </p>
 *
 * @author Chuanwise
 */
public interface PluginLoadingContext {

    /**
     * Get cause.
     *
     * @return cause
     */
    Cause getCause();

    /**
     * Get cabin.
     *
     * @return cabin
     */
    Cabin getCabin();
}
