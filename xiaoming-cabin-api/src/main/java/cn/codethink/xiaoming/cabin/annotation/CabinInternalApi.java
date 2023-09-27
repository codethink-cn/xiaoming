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

package cn.codethink.xiaoming.cabin.annotation;

import cn.codethink.xiaoming.annotation.BotInternalApi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <h1>Cabin Internal Api</h1>
 *
 * <p>Just like {@link BotInternalApi}, the cabin internal api is
 * very low-level api in xiaoming. They are written for developing
 * xiaoming internal functions. </p>
 *
 * <p>This annotation on functions, classes, interfaces and
 * enumerate classes means they and related apis are internal. </p>
 *
 * @apiNote Don't call them directly unless you are developing
 * internal functions of xiaoming cabin.
 * @author Chuanwise
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface CabinInternalApi {
}
