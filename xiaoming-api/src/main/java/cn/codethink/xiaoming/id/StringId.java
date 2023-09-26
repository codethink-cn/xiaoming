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

import cn.codethink.xiaoming.api.BotApiFactory;

/**
 * <h1>String Id</h1>
 *
 * <p>String id is based on string. </p>
 *
 * @author Chuanwise
 */
public interface StringId
    extends Id, Comparable<StringId> {

    /**
     * Construct a string id with provided value.
     *
     * @param value value
     * @return string id
     */
    static StringId of(long value) {
        return BotApiFactory.getBotApi().getStringId(value);
    }
}
