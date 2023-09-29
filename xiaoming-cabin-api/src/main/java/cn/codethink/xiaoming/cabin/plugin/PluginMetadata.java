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

import cn.codethink.xiaoming.id.NamespaceId;
import cn.codethink.xiaoming.namespace.Namespace;
import org.semver4j.Semver;

/**
 * <h1>Plugin Metadata</h1>
 *
 * <p>Plugin metadata are a description of a plugin loaded by {@link PluginScanner}. </p>
 *
 * @author Chuanwise
 */
public interface PluginMetadata {

    /**
     * Get id.
     *
     * @return id
     */
    NamespaceId getId();

    /**
     * Get name.
     *
     * @return name
     */
    String getName();

    /**
     * Get namespace.
     *
     * @return namespace
     */
    Namespace getNamespace();

    /**
     * Get version.
     *
     * @return version
     */
    Semver getVersion();
}
