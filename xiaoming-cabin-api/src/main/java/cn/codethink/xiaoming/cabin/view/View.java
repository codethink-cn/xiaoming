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

import cn.codethink.xiaoming.cabin.api.CabinApiFactory;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <h1>View</h1>
 *
 * <p>View is a property. </p>
 *
 * @author Chuanwise
 */
public interface View<T>
    extends Supplier<T> {

    /**
     * Construct a view with provided getter and setter method.
     *
     * @param getter getter
     * @param setter setter
     * @param filter filter
     * @return view
     * @param <T> view type
     */
    static <T> View<T> with(Supplier<T> getter, Function<T, Boolean> setter, Predicate<T> filter) {
        return CabinApiFactory.getCabinApi().getViewWith(getter, setter, filter);
    }

    /**
     * Construct a read only view with provided getter.
     *
     * @param getter getter
     * @return view
     * @param <T> view type
     */
    static <T> View<T> with(Supplier<T> getter) {
        return CabinApiFactory.getCabinApi().getViewWith(getter);
    }

    /**
     * Get value.
     *
     * @return value
     */
    @Override
    T get();

    /**
     * Map view to other one.
     *
     * @param mapper mapper
     * @param <U>    mapped view type
     * @return mapped view
     * @throws NullPointerException mapper is null
     */
    <U> View<U> map(Function<T, U> mapper);
}