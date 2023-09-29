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

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LongIdImpl
    implements LongId {

    private final long value;
    private Integer hashCodeCache;
    private String toStringCache;

    public LongIdImpl(long value) {
        this.value = value;
    }

    @Override
    public long toLong() {
        return value;
    }

    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = Long.toString(value);
        }
        return toStringCache;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LongId)) {
            return false;
        }
        final LongId longId = (LongId) obj;
        return longId.toLong() == value;
    }

    @Override
    public int compareTo(@NonNull LongId o) {
        Preconditions.checkNotNull(o, "Long id is null! ");

        return Long.compare(value, o.toLong());
    }

    @Override
    public int hashCode() {
        if (hashCodeCache == null) {
            hashCodeCache = Long.hashCode(value);
        }
        return hashCodeCache;
    }
}
