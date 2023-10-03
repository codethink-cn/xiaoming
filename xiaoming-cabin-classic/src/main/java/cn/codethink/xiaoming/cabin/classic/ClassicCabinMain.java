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

package cn.codethink.xiaoming.cabin.classic;

import cn.codethink.xiaoming.cabin.classic.plugin.JavaPluginMain;
import cn.codethink.xiaoming.cabin.plugin.PluginLoadingContext;

import java.io.IOException;

public class ClassicCabinMain
        extends JavaPluginMain {

    private static final ClassicCabinMain INSTANCE = new ClassicCabinMain();

    public static ClassicCabinMain getInstance() {
        return INSTANCE;
    }

    private volatile ClassicPluginConfiguration configuration;

    public ClassicPluginConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void load(PluginLoadingContext context) throws Exception {
        super.load(context);
    }

    public void reloadData() throws IOException {

    }
}
