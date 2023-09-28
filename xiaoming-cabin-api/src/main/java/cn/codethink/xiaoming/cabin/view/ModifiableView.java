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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <h1>Modifiable View</h1>
 *
 * <p>Modifiable view is the modifiable version of {@link View}. </p>
 *
 * @param <T> view type
 * @author Chuanwise
 */
public interface ModifiableView<T>
    extends View<T>, Predicate<T>, Consumer<T> {

    /**
     * Set value.
     *
     * @param value value
     * @return if value changed
     */
    boolean set(T value);

    /**
     * Set value.
     *
     * @param value value
     * @return if value changed
     * @throws IllegalArgumentException value is illegal
     */
    boolean setOrFail(T value);

    /**
     * Test if provided value is legal for the value.
     *
     * @param value value
     * @return if provided value is legal
     */
    @Override
    boolean test(T value);

    /**
     * Map view to other one.
     *
     * @param mapper         mapper
     * @param reversedMapper reversed mapper
     * @param <U>            mapped view type
     * @return mapped view
     * @throws NullPointerException mapper or reversed mapper is null
     */
    <U> ModifiableView<U> map(Function<T, U> mapper, Function<U, T> reversedMapper);
}
