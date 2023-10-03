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
import org.semver4j.RangesExpression;

/**
 * <h1>Plugin Dependency</h1>
 *
 * @author Chuanwise
 */
public interface PluginDependency {

    /**
     * Get id.
     *
     * @return id
     */
    NamespaceId getId();

    /**
     * Get expression.
     *
     * @return expression or null represents any version.
     */
    RangesExpression getVersionExpression();

    /**
     * Query if the dependency is optional.
     *
     * @return if the dependency is optional
     */
    boolean isOptional();
}
