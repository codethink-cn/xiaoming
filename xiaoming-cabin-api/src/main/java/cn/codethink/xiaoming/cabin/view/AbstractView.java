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

import cn.codethink.xiaoming.cause.Cause;

import java.util.Objects;
import java.util.function.Function;

/**
 * <h1>Abstract View</h1>
 *
 * <p>Abstract view is a simple implementation of {@link View} for
 * developers to inherit from. </p>
 *
 * <p>Not all views must be inherited from this class, but if you are
 * developing a simple view, it's recommended to inherit from it. </p>
 *
 * @see View
 * @author Chuanwise
 */
public abstract class AbstractView<T>
    implements View<T> {

    private class MappedView<U>
        extends AbstractView<U> {

        private final Function<T, U> mapper;

        public MappedView(Function<T, U> mapper) {
            this.mapper = mapper;
        }

        @Override
        public U get() {
            return mapper.apply(AbstractView.this.get());
        }
    }

    @Override
    public <U> View<U> map(Function<T, U> mapper) {
        return new MappedView<>(mapper);
    }

    @Override
    public boolean equals(Object obj) {
        return get().equals(obj);
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public String toString() {
        return Objects.toString(get());
    }
}
