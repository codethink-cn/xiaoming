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

package cn.codethink.xiaoming.cabin;

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cabin.configuration.CabinConfiguration;
import cn.codethink.xiaoming.cabin.event.EventManager;
import cn.codethink.xiaoming.cabin.plugin.PluginManager;
import cn.codethink.xiaoming.cause.Cause;
import cn.codethink.xiaoming.state.State;
import cn.codethink.xiaoming.state.StateImpl;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;

public class CabinImpl
    implements Cabin {

    private volatile State<CabinStateType> state = new StateImpl<>(CabinStateType.READY, Cause.of("Constructing cabin"));

    private static final Logger LOGGER = LoggerFactory.getLogger(Cabin.class);

    private volatile CabinConfiguration configuration;

    @Override
    public CabinConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public EventManager getEventManager() {
        return null;
    }

    @Override
    public PluginManager getPluginManager() {
        return null;
    }

    @Override
    public void start(Cause cause, Subject subject) throws Exception {
        Preconditions.checkNotNull(cause, "Cause is null! ");
        Preconditions.checkNotNull(subject, "Subject is null! ");

        // check if the cabin can be started now
        final CabinStateType stateType = state.getType();
        final Cause stateCause = state.getCause();
        switch (stateType) {
            case STOPPED:
            case READY:
            case STARTING_ERROR:
                break;
            case STARTING:
                throw new UnsupportedOperationException("Concurrent starting because '" + cause.getDescription() + "' " +
                        "at '" + stateCause.getTime() + "'! ");
            case STARTED: {
                throw new UnsupportedOperationException("Cabin is already started because '" + stateCause.getDescription() + "' " +
                        "at " + stateCause.getTime() + "! ");
            }
            default:
                throw new UnsupportedOperationException("Unexpected state type: '" + stateType + "'! ");
        }

        final CabinStartingEvent event = new CabinStartingEventImpl(cause, subject, this);
        if (event.isCancelled()) {
            throw new CancellationException("Operation of cabin starting was cancelled by ''");
        }

        state = new StateImpl<>(CabinStateType.STARTING, cause);
    }

    @Override
    public void stop(Cause cause, Subject subject) throws Exception {

    }

    @Override
    public String getName() {
        return configuration.getName();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public State<CabinStateType> getState() {
        return state;
    }
}
