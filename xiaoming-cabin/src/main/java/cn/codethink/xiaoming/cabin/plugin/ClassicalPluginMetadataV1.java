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

import cn.codethink.xiaoming.id.Id;
import cn.codethink.xiaoming.namespace.Namespace;
import com.google.common.base.Preconditions;
import org.semver4j.Semver;

import java.util.regex.Pattern;

public class ClassicalPluginMetadataV1
    implements PluginMetadata {

    Pattern NAME_PATTERN = Pattern.compile("[\\w$\\-]+");

    private final Namespace namespace;
    private final String name;
    private final Semver version;

    ClassicalPluginMetadataV1(Namespace namespace, String name, Semver version) {
        Preconditions.checkNotNull(namespace, "Namespace is null!");
        Preconditions.checkNotNull(name, "Name is null!");
        Preconditions.checkNotNull(version, "Version is null!");
        Preconditions.checkArgument(!name.isEmpty(), "Name is empty! ");

        this.namespace = namespace;
        this.name = name;
        this.version = version;
    }

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Namespace getNamespace() {
        return null;
    }

    @Override
    public Semver getVersion() {
        return null;
    }
}
