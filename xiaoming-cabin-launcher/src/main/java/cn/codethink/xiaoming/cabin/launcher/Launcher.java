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

package cn.codethink.xiaoming.cabin.launcher;

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cabin.Cabin;
import cn.codethink.xiaoming.cabin.CabinImpl;
import cn.codethink.xiaoming.cabin.launcher.configuration.LauncherConfiguration;
import cn.codethink.xiaoming.cabin.launcher.configuration.LauncherConfigurationV1;
import cn.codethink.xiaoming.cause.Cause;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>Launcher</h1>
 *
 * @author Chuanwise
 */
public class Launcher
    implements Subject {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    /**
     * Cabin.
     */
    private volatile Cabin cabin;

    /**
     * Launcher configuration.
     */
    private volatile LauncherConfiguration launcherConfiguration = new LauncherConfigurationV1();

    /**
     * Launcher state.
     */
    private volatile LauncherState state = new LauncherState(LauncherState.Type.READY, Cause.of("Constructing launcher"));

    @Override
    public String getName() {
        return launcherConfiguration.getName();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Enable launcher.
     *
     * @param cause cause
     * @throws Exception exception thrown in enabling launcher
     */
    public void enable(Cause cause) throws Exception {
        Preconditions.checkNotNull(cause, "Cause is null!");

        // TODO: can enable checking

        final LauncherEnablingCause launcherEnablingCause = new LauncherEnablingCause(cause, this);
        cabin = new CabinImpl();
        cabin.getConfiguration().getFolderConfiguration().setWorkingDirectoryPath(launcherConfiguration.getWorkingDirectoryPath());
    }
}
