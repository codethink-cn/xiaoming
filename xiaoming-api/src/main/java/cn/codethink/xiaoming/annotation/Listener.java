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

package cn.codethink.xiaoming.annotation;

import cn.codethink.xiaoming.event.Order;
import cn.codethink.xiaoming.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>Listener</h1>
 *
 * <p>@Listener is for annotating listener methods in a class to
 * be registered as event listeners. </p>
 *
 * @author Chuanwise
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {

    /**
     * Related event classes of the listener. If it is empty, it
     * will be the type of the unique event method parameter.
     *
     * @return classes
     */
    Class<?>[] value() default {};

    /**
     * Order of the listener.
     *
     * @return order
     */
    Order order() default Order.DEFAULT;

    /**
     * Query if cancelled events are ignored by the listener.
     *
     * @return If cancelled events are ignored by the listener.
     */
    boolean ignoreCancelledEvent() default true;
}
