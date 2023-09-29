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

package cn.codethink.xiaoming.cabin.classic.plugin;

import cn.codethink.xiaoming.cabin.plugin.PluginMetadata;

/**
 * <h1>Classic Plugin Metadata</h1>
 *
 * <p>An legal classic plugin should be a jar, which resource file:
 * 'META-INF/classic-cabin/plugin.yml' should contains key-value pairs as
 * follows: </p>
 *
 * <ul>
 *     <li>name: plugin name, can only contains english characters, digits,
 *     dollar '$' and underline '_'. </li>
 *
 *     <li>namespace: plugin namespace, for isolating different same name
 *     plugins,  it always be a inverted domain name related to the author(s)
 *     or their organization, consists of substrings containing english
 *     characters, digits, dollar '$' and underline '_' separated by dot '.'.
 *     such as 'cn.codethink.xiaoming.cabin'. </li>
 *
 *     <li>version: plugin version, for isolating different version of plugins,
 *     following the <a href="https://semver.org/lang/zh-CN/">Apache Semantic
 *     Versioning</a> standard. </li>
 * </ul>
 *
 * @author Chuanwise
 */
public interface ClassicPluginMetadata
    extends PluginMetadata {
}
