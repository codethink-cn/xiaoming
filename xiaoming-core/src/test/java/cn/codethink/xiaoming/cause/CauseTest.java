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

package cn.codethink.xiaoming.cause;

import org.junit.jupiter.api.Test;

public class CauseTest {

    @Test
    void testChainedDescription() {
        final Cause groupMessageReceived = Cause.of("Group message received");
        final Cause commandExecuted = Cause.of("Command executed", groupMessageReceived);
        final Cause botStopped = Cause.of("Bot stopped", commandExecuted);

        botStopped.printCauseTrace();
    }
}
