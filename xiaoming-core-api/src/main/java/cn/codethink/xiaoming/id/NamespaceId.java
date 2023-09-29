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
import cn.codethink.xiaoming.namespace.Namespace;

/**
 * <h1>Namespace Id</h1>
 *
 * <p>Namespace id is an identifier localed in a logical namespace.
 * Such as 'cn.codethink.lexicon', whose namespace is 'cn.codethink'
 * and name is 'lexicon'. </p>
 *
 * @author Chuanwise
 */
public interface NamespaceId
    extends Id {

    /**
     * Parse a provided string to a namespace id.
     *
     * @param namespaceId string
     * @return namespace id
     * @throws NullPointerException     namespace id is null
     * @throws IllegalArgumentException namespace id is illegal
     */
    static NamespaceId parse(String namespaceId) {
        return BotApiFactory.getBotApi().parseNamespaceId(namespaceId);
    }

    /**
     * Get namespace.
     *
     * @return namespace
     */
    Namespace getNamespace();

    /**
     * Get name.
     *
     * @return name
     */
    String getName();
}
