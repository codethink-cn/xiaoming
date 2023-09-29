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
import cn.codethink.xiaoming.cabin.view.ModifiableView;
import cn.codethink.xiaoming.cause.Cause;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

/**
 * <h1>Abstract Plugin</h1>
 *
 * <p>Abstract plugin is a simple implementation of {@link Plugin} for
 * developers to inherit from. </p>
 *
 * <p>Not all plugins must be inherited from this class, but if you are
 * developing a simple plugin, it's recommended to inherit from it. </p>
 *
 * @see ModifiableView
 * @author Chuanwise
 */
public abstract class AbstractPlugin
    implements Plugin {

    @Override
    public void load(Cause cause, Subject subject) throws Exception {
        Preconditions.checkNotNull(cause, "Cause is null!");
    }

    @Override
    public void enable(Cause cause, Subject subject) throws Exception {

    }

    @Override
    public void disable(Cause cause, Subject subject) throws Exception {

    }
}
