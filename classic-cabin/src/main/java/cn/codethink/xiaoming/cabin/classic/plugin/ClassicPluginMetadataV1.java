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
import cn.codethink.xiaoming.id.NamespaceId;
import cn.codethink.xiaoming.namespace.Namespace;
import com.google.common.base.Preconditions;
import org.semver4j.Semver;

import java.util.regex.Pattern;

public class ClassicPluginMetadataV1
    implements PluginMetadata {

    Pattern NAME_PATTERN = Pattern.compile("[\\w$\\-]+");

    private final NamespaceId id;
    private final Semver version;

    ClassicPluginMetadataV1(NamespaceId id, Semver version) {
        Preconditions.checkNotNull(id, "Namespace id is null!");
        Preconditions.checkNotNull(version, "Version is null!");

        this.id = id;
        this.version = version;
    }

    @Override
    public NamespaceId getId() {
        return id;
    }

    @Override
    public String getName() {
        return id.getName();
    }

    @Override
    public Namespace getNamespace() {
        return id.getNamespace();
    }

    @Override
    public Semver getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClassicPluginMetadataV1)) {
            return false;
        }
        final ClassicPluginMetadataV1 classicPluginMetadataV1 = (ClassicPluginMetadataV1) obj;
        throw new UnsupportedOperationException();
    }
}
