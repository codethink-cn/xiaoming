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

package cn.codethink.xiaoming.id;

import cn.codethink.xiaoming.namespace.Namespace;
import cn.codethink.xiaoming.namespace.NamespaceImpl;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class NamespaceIdImpl
    implements NamespaceId {

    private static final Pattern PATTERN = Pattern.compile("[\\w$\\-]+(.[\\w$\\-]+)+");

    private final Namespace namespace;
    private final String name;
    private String toStringCache;

    public NamespaceIdImpl(Namespace namespace, String name) {
        Preconditions.checkNotNull(namespace, "Namespace is null!");
        Preconditions.checkNotNull(name, "Name is null!");

        this.namespace = namespace;
        this.name = name;
    }

    public static NamespaceIdImpl parse(String namespaceId) {
        Preconditions.checkNotNull(namespaceId, "Namespace id is null!");
        Preconditions.checkArgument(PATTERN.matcher(namespaceId).matches(), "Namespace id '" + namespaceId + "' is illegal!");

        final String[] strings = namespaceId.split(Pattern.quote("."));
        final String[] namespaceSegmentsArray = Arrays.copyOf(strings, strings.length - 1);
        final List<String> namespaceSegmentsList = Collections.unmodifiableList(Arrays.asList(namespaceSegmentsArray));

        final String namespaceString = namespaceId.substring(0, namespaceId.length() - strings[strings.length - 1].length() - 1);
        return new NamespaceIdImpl(new NamespaceImpl(namespaceString, namespaceSegmentsList), strings[strings.length - 1]);
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NamespaceId)) {
            return false;
        }
        final NamespaceId namespaceId = (NamespaceId) obj;
        return namespace.equals(namespaceId.getNamespace())
                && name.equals(namespaceId.getName());
    }

    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = namespace + "." + name;
        }
        return toStringCache;
    }
}
