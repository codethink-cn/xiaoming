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

package cn.codethink.xiaoming.state;

/**
 * <h1>State Type</h1>
 *
 * @author Chuanwise
 */
public interface StateType {

    int ERROR_MASK = 1;
    int DOING_MASK = 1 << 1;
    int DONE_MASK = 1 << 2;

    /**
     * Query if the state type represents something error.
     *
     * @return if the state type represents something error
     */
    default boolean isError() {
        return (getMask() & ERROR_MASK) == ERROR_MASK;
    }

    /**
     * Query if the state type represents something doing.
     *
     * @return if the state type represents something doing
     */
    default boolean isDoing() {
        return (getMask() & DOING_MASK) == DOING_MASK;
    }

    /**
     * Query if the state type represents something done.
     *
     * @return if the state type represents something done
     */
    default boolean isDone() {
        return (getMask() & DONE_MASK) == DONE_MASK;
    }

    /**
     * Get mask.
     *
     * @return mask
     */
    int getMask();
}
