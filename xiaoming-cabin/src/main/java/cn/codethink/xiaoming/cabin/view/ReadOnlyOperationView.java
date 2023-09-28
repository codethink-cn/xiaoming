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

import java.util.function.Supplier;

public class ReadOnlyOperationView<T>
    extends AbstractView<T> {

    private final Supplier<T> getter;

    public ReadOnlyOperationView(Supplier<T> getter) {
        Preconditions.checkNotNull(getter, "Getter is null!");

        this.getter = getter;
    }

    @Override
    public T get() {
        return getter.get();
    }

    @Override
    public boolean set(T value) {
        return false;
    }

    @Override
    public boolean setOrFail(T value) {
        throw new UnsupportedOperationException("Value is read only!");
    }

    @Override
    public boolean isLegal(T value) {
        return false;
    }
}
