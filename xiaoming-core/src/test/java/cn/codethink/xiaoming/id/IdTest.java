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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class IdTest {

    @CsvSource({
            "cn.codethink.xiaoming.cabin.lexicon, cn.codethink.xiaoming.cabin, lexicon",
            "cn.codethink.xiaoming.cabin.chat-commands, cn.codethink.xiaoming.cabin, chat-commands",
            "cn.codethink.xiaoming.cabin.essentials, cn.codethink.xiaoming.cabin, essentials",
            "cn.chuanwise.xiaoming.cabin.xiaoming-minecraft, cn.chuanwise.xiaoming.cabin, xiaoming-minecraft",
            "com.example$name.name, com.example$name, name",
    })
    @ParameterizedTest
    void testNamespaceIdParsing(String namespaceId, String namespace, String name) {
        final NamespaceId parsed = NamespaceId.parse(namespaceId);
        Assertions.assertEquals(namespace, parsed.getNamespace().toString());
        Assertions.assertEquals(name, parsed.getName());
    }
}
