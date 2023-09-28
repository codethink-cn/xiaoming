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

/**
 * <h1>Abstract Modifiable View</h1>
 *
 * <p>Abstract modifiable view is a simple implementation of {@link ModifiableView} for
 * developers to inherit from. </p>
 *
 * <p>Not all modifiable views must be inherited from this class, but if you are
 * developing a simple modifiable view, it's recommended to inherit from it. </p>
 *
 * @see ModifiableView
 * @author Chuanwise
 */
public abstract class AbstractModifiableView<T>
    extends AbstractView<T>
    implements ModifiableView<T> {

    private class MappedModifiableView<U>
        extends AbstractModifiableView<U> {

        private final Function<T, U> mapper;
        private final Function<U, T> reversedMapper;

        public MappedModifiableView(Function<T, U> mapper, Function<U, T> reversedMapper) {
            this.mapper = mapper;
            this.reversedMapper = reversedMapper;
        }

        @Override
        public boolean set(U value) {
            return AbstractModifiableView.this.set(reversedMapper.apply(value));
        }

        @Override
        public boolean setOrFail(U value) {
            return AbstractModifiableView.this.setOrFail(reversedMapper.apply(value));
        }

        @Override
        public boolean test(U value) {
            return AbstractModifiableView.this.test(reversedMapper.apply(value));
        }

        @Override
        public U get() {
            return mapper.apply(AbstractModifiableView.this.get());
        }
    }

    @Override
    public void accept(T t) {
        setOrFail(t);
    }

    @Override
    public <U> ModifiableView<U> map(Function<T, U> mapper, Function<U, T> reversedMapper) {
        Preconditions.checkNotNull(mapper, "Mapper is null!");
        Preconditions.checkNotNull(reversedMapper, "Reversed mapper is null!");

        return new MappedModifiableView<>(mapper, reversedMapper);
    }
}
