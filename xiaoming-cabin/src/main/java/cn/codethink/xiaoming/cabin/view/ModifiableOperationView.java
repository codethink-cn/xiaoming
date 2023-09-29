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

package cn.codethink.xiaoming.cabin.view;

import com.google.common.base.Preconditions;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModifiableOperationView<T>
    extends AbstractModifiableView<T> {

    private final Supplier<T> getter;
    private final Function<T, Boolean> setter;
    private final Predicate<T> filter;

    public ModifiableOperationView(Supplier<T> getter, Function<T, Boolean> setter, Predicate<T> filter) {
        Preconditions.checkNotNull(getter, "Getter is null! ");
        Preconditions.checkNotNull(setter, "Setter is null! ");
        Preconditions.checkNotNull(filter, "Filter is null! ");

        this.getter = getter;
        this.setter = setter;
        this.filter = filter;
    }

    @Override
    public T get() {
        return getter.get();
    }

    @Override
    public boolean set(T value) {
        if (!filter.test(value)) {
            return false;
        }
        return setter.apply(value);
    }

    @Override
    public boolean setOrFail(T value) {
        if (!filter.test(value)) {
            throw new IllegalArgumentException("Value is illegal!");
        }
        return setter.apply(value);
    }

    @Override
    public boolean test(T value) {
        return filter.test(value);
    }
}
