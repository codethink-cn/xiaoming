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

import com.google.common.base.Preconditions;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * <h1>Plugin Class Loader</h1>
 *
 * <p>Plugin class loader is used to load classes that plugin required.
 * If the developer needs another class loader, its parent class loader
 * must be set to this class loader. </p>
 *
 * @author Chuanwise
 */
public class PluginClassLoader
    extends URLClassLoader {

    /**
     * Empty url array for constructors of parent class.
     */
    private static final URL[] EMPTY_URL_ARRAY = {};

    /**
     * Corresponding plugin.
     */
    private final Plugin plugin;

    public PluginClassLoader(Plugin plugin, ClassLoader parent) {
        super(EMPTY_URL_ARRAY, parent);

        Preconditions.checkNotNull(plugin, "Plugin is null! ");

        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void addURL(URL url) {
        Preconditions.checkNotNull(url, "URL is null! ");

        super.addURL(url);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PluginClassLoader)) {
            return false;
        }
        final PluginClassLoader pluginClassLoader = (PluginClassLoader) obj;
        return plugin.equals(pluginClassLoader.plugin);
    }
}
