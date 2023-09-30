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

import cn.codethink.xiaoming.state.StateType;

/**
 * <h1>Bot State Type</h1>
 *
 * <p>Type of bot state. </p>
 *
 * @author Chuanwise
 */
public enum BotStateType
        implements StateType {

    /**
     * Ready for starting, but never been started after constructed.
     */
    READY,

    /**
     * Starting.
     */
    STARTING(DOING_MASK),

    /**
     * Error occurred in starting bot.
     */
    STARTING_ERROR(ERROR_MASK),

    /**
     * Started.
     */
    STARTED(DONE_MASK),

    /**
     * Stopping.
     */
    STOPPING(DOING_MASK),

    /**
     * Error occurred in stopping bot.
     */
    STOPPING_ERROR(ERROR_MASK),

    /**
     * Stopped.
     */
    STOPPED(DONE_MASK);

    private final int mask;

    BotStateType(int mask) {
        this.mask = mask;
    }

    BotStateType() {
        this.mask = 0;
    }

    @Override
    public int getMask() {
        return mask;
    }
}