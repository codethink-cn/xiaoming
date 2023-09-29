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

package cn.codethink.xiaoming.namespace;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class NamespaceImpl
    implements Namespace {

    private static final String[] EMPTY_STRING_ARRAY = {};

    private final String namespace;
    private final List<String> segments;

    public NamespaceImpl(String namespace, List<String> segments) {
        this.namespace = namespace;
        this.segments = segments;
    }

    public static NamespaceImpl parse(String namespace) {
        Preconditions.checkNotNull(namespace, "Namespace is null! ");
        Preconditions.checkArgument(PATTERN.matcher(namespace).matches(), "Namespace is illegal! Make sure " +
                "it matches the regexp: '" + PATTERN + "'! ");

        return new NamespaceImpl(namespace, Collections.unmodifiableList(Arrays.asList(namespace.split(Pattern.quote(".")))));
    }

    @Override
    public int size() {
        return segments.size();
    }

    @Override
    public String[] toArray() {
        return segments.toArray(EMPTY_STRING_ARRAY);
    }

    @Override
    public List<String> toList() {
        return segments;
    }

    @Override
    @NonNull
    public Iterator<String> iterator() {
        return segments.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Namespace)) {
            return false;
        }
        return namespace.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return namespace.hashCode();
    }

    @Override
    public String toString() {
        return namespace;
    }
}
