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

package cn.codethink.xiaoming.cabin.classic.plugin;

import cn.codethink.xiaoming.cabin.plugin.*;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

public class ClassicPlugin
    extends AbstractPlugin {

    @Override
    public PluginMain getMain() {
        return null;
    }

    @Override
    public PluginState getState() {
        return null;
    }

    @Override
    public PluginMetadata getMetadata() {
        return null;
    }

    @Override
    public PluginScanner getScanner() {
        return null;
    }

    @Override
    public PluginClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Path getDataFolderPath() {
        return null;
    }

    @Override
    public File getDataFolderFile() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }
}
