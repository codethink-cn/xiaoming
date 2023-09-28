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

package cn.codethink.xiaoming.cabin.configuration;

import java.io.File;
import java.nio.file.Path;

/**
 * <h1>Cabin Configuration</h1>
 *
 * @author Chuanwise
 */
public interface CabinConfiguration {

    /**
     * Get name.
     *
     * @return name
     */
    String getName();

    /**
     * Set name.
     *
     * @param name name
     */
    void setName(String name);

    /**
     * Get working directory file.
     *
     * @return working directory file
     */
    File getWorkingDirectoryFile();

    /**
     * Get working directory path.
     *
     * @return working directory path
     */
    Path getWorkingDirectoryPath();

    /**
     * Set working directory file.
     *
     * @param workingDirectoryFile working directory file
     */
    void setWorkingDirectoryFile(File workingDirectoryFile);

    /**
     * Set working directory path.
     *
     * @param workingDirectoryPath working directory path
     */
    void setWorkingDirectoryPath(File workingDirectoryPath);
}
