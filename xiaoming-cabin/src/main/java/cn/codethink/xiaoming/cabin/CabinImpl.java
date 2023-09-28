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
import cn.codethink.xiaoming.cause.Cause;
import cn.codethink.xiaoming.cause.ExceptionCause;
import cn.codethink.xiaoming.cause.FailedCause;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;

public class CabinImpl
    implements Cabin {

    private volatile CabinState state = new CabinStateImpl(CabinState.Type.READY, Cause.of("Constructing cabin"));

    private static final Logger LOGGER = LoggerFactory.getLogger(Cabin.class);

    private volatile CabinConfiguration configuration;

    @Override
    public CabinConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void start(Cause cause, Subject subject) throws Exception {
        Preconditions.checkNotNull(cause, "Cause is null!");
        Preconditions.checkNotNull(subject, "Subject is null!");

        // check if the cabin can be started now
        final CabinState.Type stateType = state.getType();
        final Cause stateCause = state.getCause();
        switch (stateType) {
            case STOPPED:
            case READY:
                break;
            case STARTING: {
                throw new UnsupportedOperationException("Concurrent starting because '" + cause.getDescription() + "' " +
                        "at '" + stateCause.getTime() + "'! ");
            }
            case STARTING_FAILED: {
                final FailedCause failedCause = (FailedCause) stateCause;
                if (failedCause.isRetryable()) {
                    break;
                } else if (failedCause instanceof ExceptionCause) {
                    final ExceptionCause exceptionCause = (ExceptionCause) failedCause;
                    throw new UnsupportedOperationException("Error occurred during the last starting of cabin " +
                            "'" + configuration.getName() + "' at " + failedCause.getTime() +  ", and it's not retryable! ", exceptionCause.getException());
                } else {
                    throw new UnsupportedOperationException("Error occurred during the last starting of cabin " +
                            "'" + configuration.getName() + "' at " + failedCause.getTime() +  ", and it's not retryable! ");
                }
            }
            case STARTED: {
                throw new UnsupportedOperationException("Cabin is already started because '" + stateCause.getDescription() + "' " +
                        "at " + stateCause.getTime() + "! ");
            }
            case STOPPING: {
                throw new UnsupportedOperationException("Cabin is stopping because '' " + stateCause.getTime() + "! ");
            }
            case STOPPING_FAILED: {
                final FailedCause failedCause = (FailedCause) stateCause;
                if (failedCause.isRetryable()) {
                    throw new UnsupportedOperationException("Cabin hadn't stopped completely because '" + cause.getDescription() + "' " +
                            "at '" + stateCause.getTime() + "'! But it's retryable, use 'cabin.stop(cause, subject)' to retry becore " +
                            "calling the start method. ");
                } else {
                    throw new UnsupportedOperationException("Error occurred during the last stopping of cabin " +
                            "'" + configuration.getName() + "' at " + failedCause.getTime() +  ", and it's not retryable! ");
                }
            }
            default:
                throw new UnsupportedOperationException("Unexpected state type: '" + stateType + "'! ");
        }

        final CabinStartingEvent event = new CabinStartingEventImpl(cause, subject, this);
        if (event.isCancelled()) {
            throw new CancellationException("Operation of cabin starting was cancelled by ''");
        }

        state = new CabinStateImpl(CabinState.Type.STARTING, cause);
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
}
