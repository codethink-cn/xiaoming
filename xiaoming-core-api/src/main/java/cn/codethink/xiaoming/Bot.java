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

package cn.codethink.xiaoming;

import cn.codethink.xiaoming.event.EventManager;
import cn.codethink.xiaoming.relation.Known;

/**
 * <h1>Bot</h1>
 *
 * <p>Bot is the robot itself. </p>
 *
 * @author Chuanwise
 */
public interface Bot
    extends Known {

    /**
     * Get event manager.
     *
     * @return event manager
     */
    EventManager getEventManager();
}
