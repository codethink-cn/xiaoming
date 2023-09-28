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
import cn.codethink.xiaoming.cabin.Cabin;
import cn.codethink.xiaoming.cabin.view.View;
import com.google.common.base.Preconditions;

import java.io.File;
import java.util.Collection;

public class ClassicalPluginScanner
    implements PluginScanner {

    private final Cabin cabin;

    public ClassicalPluginScanner(Cabin cabin) {
        Preconditions.checkNotNull(cabin, "Cabin is null!");

        this.cabin = cabin;

        final View<File> pluginsFileView = View.with(
                () -> new File(cabin.getConfiguration().getWorkingDirectoryFile(), "plugins")
        );
        final File pluginsFile = pluginsFileView.get();
    }

    @Override
    public Subject getSubject() {
        return cabin;
    }

    @Override
    public Collection<Plugin> scan(PluginScanningContext context) throws Exception {
        Preconditions.checkNotNull(context, "Plugin scanning context is null!");

        // TODO

        return null;
    }
}
