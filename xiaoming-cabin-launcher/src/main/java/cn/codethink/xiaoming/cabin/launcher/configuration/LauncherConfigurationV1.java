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

package cn.codethink.xiaoming.cabin.launcher.configuration;

import com.google.common.base.Preconditions;

import java.io.File;
import java.nio.file.Path;

public class LauncherConfigurationV1
    implements LauncherConfiguration {

    private String name;
    private Path workingDirectoryPath;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public File getWorkingDirectoryFile() {
        return workingDirectoryPath.toFile();
    }

    @Override
    public Path getWorkingDirectoryPath() {
        return workingDirectoryPath;
    }

    @Override
    public void setWorkingDirectoryFile(File workingDirectoryFile) {
        Preconditions.checkNotNull(workingDirectoryFile, "Working directory file is null! ");

        this.workingDirectoryPath = workingDirectoryFile.toPath();
    }

    @Override
    public void setWorkingDirectoryPath(Path workingDirectoryPath) {
        Preconditions.checkNotNull(workingDirectoryPath, "Working directory path is null! ");

        this.workingDirectoryPath = workingDirectoryPath;
    }
}
