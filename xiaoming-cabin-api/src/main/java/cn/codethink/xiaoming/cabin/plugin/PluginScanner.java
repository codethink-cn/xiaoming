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

import java.util.Collection;

/**
 * <h1>Plugin Scanner</h1>
 *
 * <p>Plugin scanner is a module to scan all available plugins
 * installed in the instance of xiaoming cabin. </p>
 *
 * @author Chuanwise
 */
public interface PluginScanner {

    /**
     * Get subject.
     *
     * @return subject
     */
    Subject getSubject();

    /**
     * Scan all available plugins.
     *
     * @param context context
     * @return all available plugins
     * @throws Exception exception thrown in scanning plugins
     */
    Collection<Plugin> scan(PluginScanningContext context) throws Exception;
}
