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

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cabin.Cabin;
import cn.codethink.xiaoming.cabin.plugin.Plugin;
import cn.codethink.xiaoming.cabin.plugin.PluginMetadata;
import cn.codethink.xiaoming.cabin.plugin.PluginScanner;
import cn.codethink.xiaoming.cabin.plugin.PluginScanningContext;
import cn.codethink.xiaoming.cabin.util.Files;
import cn.codethink.xiaoming.cabin.view.View;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class ClassicPluginScanner
    implements PluginScanner {

    private static final String METADATA_FILE_PATH = "xiaoming/plugin.yml";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassicPluginScanner.class);

    private final Cabin cabin;
    private final View<File> pluginsDirectoryFileView;

    public ClassicPluginScanner(Cabin cabin) {
        Preconditions.checkNotNull(cabin, "Cabin is null!");

        this.cabin = cabin;
        this.pluginsDirectoryFileView = View.with(
                () -> cabin.getConfiguration().getFolderConfiguration().getPluginsDirectoryFile()
        );
    }

    @Override
    public Subject getSubject() {
        return cabin;
    }

    @Override
    public Collection<Plugin> scan(PluginScanningContext context)  {
        Preconditions.checkNotNull(context, "Plugin scanning context is null!");

        final File pluginsDirectoryFile = pluginsDirectoryFileView.get();
        if (!pluginsDirectoryFile.isDirectory()) {
            LOGGER.warn("Plugin directory '" + pluginsDirectoryFile.toPath() + "' isn't present! ");
            return Collections.emptyList();
        }

        final File[] filesInPluginsDirectory = pluginsDirectoryFile.listFiles();
        if (filesInPluginsDirectory == null) {
            LOGGER.error("Files in plugin directory '" + pluginsDirectoryFile.toPath() + "' got by 'listFiles()' is null! ");
            return Collections.emptyList();
        }

        for (final File fileInPluginDirectory : filesInPluginsDirectory) {
            final String fileInPluginDirectoryName = fileInPluginDirectory.getName();
            if (fileInPluginDirectory.isDirectory()) {

            } else {
                if (!fileInPluginDirectoryName.endsWith(".jar")) {
                    LOGGER.warn("The name of file '" + fileInPluginDirectoryName + "' in plugin directory " +
                            "doesn't ends with '.jar', which is suffix of classic plugin jar. ");
                    continue;
                }

                // check if it's valid classic jar file
                final PluginMetadata pluginMetadata;
                try (final JarFile jarFile = new JarFile(fileInPluginDirectoryName)) {
                    final ZipEntry metadataFileZipEntry = jarFile.getEntry(METADATA_FILE_PATH);
                    if (metadataFileZipEntry == null) {
                        LOGGER.warn("There is no plugin metadata resource file '" + METADATA_FILE_PATH + "' " +
                                "found in '" + fileInPluginDirectoryName + "' in plugin directory. The jar file will be ignored. ");
                        continue;
                    }

                    try (final InputStream inputStream = jarFile.getInputStream(metadataFileZipEntry)) {
                        pluginMetadata = readPluginMetadata(inputStream);
                    } catch (final IOException e) {
                        LOGGER.error("Fail to load the resource file '" + METADATA_FILE_PATH + "' " +
                                "found in '" + fileInPluginDirectoryName + "' in plugin directory. The jar file will be ignored. ");
                        continue;
                    }
                } catch (final IOException e) {
                    LOGGER.warn("There is no plugin metadata resource file '" + METADATA_FILE_PATH + "' " +
                            "found in '" + fileInPluginDirectoryName + "' in plugin directory. The jar file will be ignored. ");
                    continue;
                }

                // check if its directory is present
                final File pluginDirectoryFile = new File(pluginsDirectoryFile, pluginMetadata.getId().toString());
                final File pluginJarsDirectoryFile = new File(pluginDirectoryFile, "jars");
                if (pluginDirectoryFile.isDirectory()) {
                    final File pluginJarInJarsDirectoryFile = new File(pluginJarsDirectoryFile, fileInPluginDirectoryName);

                    // if file in plugin jars directory with same name
                    // is present, check if they are same.
                    // if it isn't present, move file.
                    if (pluginJarInJarsDirectoryFile.isFile()) {

                        // if they are same, print a warning
                        final boolean contentEqual;
                        try {
                            contentEqual = Files.contentEqual(pluginJarInJarsDirectoryFile, fileInPluginDirectory);
                        } catch (final IOException e) {
                            LOGGER.warn("Exception thrown in comparing the content of files with the same name '" + fileInPluginDirectoryName + "' " +
                                    "found in plugins directory and plugin directory '" + pluginMetadata.getId() + "'. " +
                                    "The one in plugins directory will be ignored. ");
                            continue;
                        }
                        if (contentEqual) {
                            LOGGER.warn("The file '" + fileInPluginDirectoryName + "' in jars directory of plugin '" + pluginMetadata.getId() + "' " +
                                    "is same as a file with same name in plugins directory. The one in plugins directory will be ignored. ");
                        } else {
                            LOGGER.warn("The file '" + fileInPluginDirectoryName + "' in jars directory of plugin '" + pluginMetadata.getId() + "' " +
                                    "isn't same as a file in plugins directory, but they has a same name. To prevent overriding file, " +
                                    "the one in plugins directory will be ignored. ");
                        }
                    } else {
                        if (!fileInPluginDirectory.renameTo(pluginJarInJarsDirectoryFile)){
                            LOGGER.warn("Can not move the plugin file '" + fileInPluginDirectoryName + "' in plugins directory to its plugin directory " +
                                    "'" + pluginMetadata.getId() + "'! This plugin will be ignored. ");
                        }
                    }
                } else {
                    throw new IllegalStateException("The directory of plugin '" + pluginMetadata.getId() + "' " +
                            "in jar file '" + fileInPluginDirectory.getName() + "' in plugins directory " +
                            "isn't present, and can not create a new one! ");
                }
            }
        }

        return null;
    }

    private static PluginMetadata readPluginMetadata(InputStream inputStream) {
        throw new UnsupportedOperationException();
    }
}
