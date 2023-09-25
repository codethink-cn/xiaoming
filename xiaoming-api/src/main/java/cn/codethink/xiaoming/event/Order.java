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

package cn.codethink.xiaoming.event;

public enum Order {

    /**
     * Initialisation and registration actions.
     */
    PRE,

    /**
     * Immediate responses to actions in PRE.
     */
    AFTER_PRE,

    /**
     * Cancellation by protection plugins for informational purposes.
     */
    FIRST,

    /**
     * Standard actions that should happen before other plugins react to the event.
     */
    EARLY,

    /**
     * The default action order.
     */
    DEFAULT,

    /**
     * Standard actions that should happen after other plugins react to the event.
     */
    LATE,

    /**
     * Final cancellation by protection plugins.
     */
    LAST,

    /**
     * Actions that need to respond to cancelled events before POST.
     */
    BEFORE_POST,

    /**
     * Actions that need to react to the final and stable effects of event.
     */
    POST,
}