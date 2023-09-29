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

import cn.codethink.xiaoming.api.BotApiFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * <h1>Namespace</h1>
 *
 * @author Chuanwise
 */
public interface Namespace
    extends Iterable<String> {

    /**
     * Pattern of namespace.
     */
    Pattern PATTERN = Pattern.compile("[\\w$\\-]+(\\.[\\w$\\-]+)*");

    /**
     * Parse a namespace
     *
     * @param namespace namespace
     * @return namespace
     * @throws NullPointerException     namespace is null
     * @throws IllegalArgumentException namespace is illegal
     */
    static Namespace parse(String namespace) {
        return BotApiFactory.getBotApi().parseNamespace(namespace);
    }

    /**
     * Get size.
     *
     * @return size
     */
    int size();

    /**
     * Convert namespace to an array.
     *
     * @return array
     */
    String[] toArray();

    /**
     * Convert namespace to a list.
     *
     * @return list
     */
    List<String> toList();
}
