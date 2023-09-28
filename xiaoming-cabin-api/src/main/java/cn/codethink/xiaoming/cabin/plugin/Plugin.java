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

package cn.codethink.xiaoming.cabin.plugin;

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cause.Cause;

import java.io.File;
import java.nio.file.Path;

/**
 * <h1>Plugin</h1>
 *
 * <p>Plugin is a set of functions NOT related to a bot directly
 * and strongly. It always be high-level, such as permission service. </p>
 *
 * @author Chuanwise
 */
public interface Plugin
    extends Subject {

    /**
     * Get state.
     *
     * @return state
     */
    PluginState getState();

    /**
     * Get metadata.
     *
     * @return metadata
     */
    PluginMetadata getMetadata();

    /**
     * Get scanner.
     *
     * @return scanner
     */
    PluginScanner getScanner();

    /**
     * Get class loader.
     *
     * @return class loader
     */
    PluginClassLoader getClassLoader();

    /**
     * Load plugin.
     *
     * @param cause cause
     * @throws Exception exception thrown in loading plugin.
     */
    void load(Cause cause) throws Exception;

    /**
     * Enable plugin.
     *
     * @param cause cause
     * @throws Exception exception thrown in enabling plugin.
     */
    void enable(Cause cause) throws Exception;

    /**
     * Disable plugin.
     *
     * @param cause cause
     * @throws Exception exception thrown in disabling plugin.
     */
    void disable(Cause cause) throws Exception;

    /**
     * Get data folder path.
     *
     * @return data folder path
     */
    Path getDataFolderPath();

    /**
     * Get data folder file.
     *
     * @return data folder file
     */
    File getDataFolderFile();
}
