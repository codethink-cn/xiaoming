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

package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Subject;
import com.google.common.base.Preconditions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EventManagerImpl
    implements EventManager {

    private static class ReflectedListener
        implements Listener {

        private static final Object[] EMPTY_ARGUMENT_ARRAY = {};

        private final Set<Class<?>> eventClasses;
        private final boolean ignoreCancelledEvent;
        private final Order order;
        private final Subject subject;
        private final Listeners object;
        private final Method method;
        private final Class<?> parameterType;

        public ReflectedListener(Set<Class<?>> eventClasses, Order order, boolean ignoreCancelledEvent, Subject subject,
                                 Listeners listeners, Method method, Class<?> parameterType) {

            this.eventClasses = eventClasses;
            this.ignoreCancelledEvent = ignoreCancelledEvent;
            this.order = order;
            this.subject = subject;
            this.object = listeners;
            this.method = method;
            this.parameterType = parameterType;
        }

        @Override
        public Set<Class<?>> getEventClasses() {
            return eventClasses;
        }

        @Override
        public void listen(EventListeningContext context) throws Exception {
            Preconditions.checkNotNull(context, "Event listening context is null!");

            final Object[] arguments;
            if (parameterType == null) {
                arguments = EMPTY_ARGUMENT_ARRAY;
            } else {
                arguments = new Object[1];
                arguments[0] = context.getEvent();
            }

            method.invoke(object, arguments);
        }

        @Override
        public boolean isIgnoreCancelledEvent() {
            return ignoreCancelledEvent;
        }

        @Override
        public Subject getSubject() {
            return subject;
        }

        @Override
        public Order getOrder() {
            return order;
        }
    }

    private final ReentrantReadWriteLock listenersLock = new ReentrantReadWriteLock();

    private final Map<Order, List<Listener>> listeners = new HashMap<>();

    @Override
    public void registerListeners(Listeners listeners, Subject subject) {
        Preconditions.checkNotNull(listeners, "Listeners are null!");
        Preconditions.checkNotNull(subject, "Subject are null!");

        final Class<? extends Listeners> listenersClass = listeners.getClass();
        final Method[] methods = listenersClass.getDeclaredMethods();

        final Map<Order, List<Listener>> listenersToBeRegistered = new HashMap<>();
        for (Method method : methods) {
            final cn.codethink.xiaoming.annotation.Listener listenerAnnotation =
                    method.getAnnotation(cn.codethink.xiaoming.annotation.Listener.class);
            if (listenerAnnotation == null) {
                continue;
            }

            final Class<?>[] eventClassesArray = listenerAnnotation.value();
            final Parameter[] parameters = method.getParameters();
            if (parameters.length > 1) {
                throw new IllegalArgumentException("Method declared in listeners class '" + listenersClass.getName() + "'" +
                        ", a listener method, has " + parameters.length + " parameters, " +
                        "but a listener method can not has more than 1 parameters. ");
            }

            // check if event classes are available
            final Set<Class<?>> eventClasses;
            Class<?> parameterType = null;
            if (eventClassesArray.length == 0) {
                if (parameters.length == 1) {

                    // if it has one parameter, parameter type must match at
                    // least one of following conditions:
                    //
                    // 1. is an interface,
                    // 2. is a non-final class,
                    // 3. can be assigned to event interface;

                    parameterType = parameters[0].getType();
                    final int parameterTypeModifiers = parameterType.getModifiers();

                    if (!Modifier.isInterface(parameterTypeModifiers)
                            && Modifier.isFinal(parameterTypeModifiers)
                            && !Event.class.isAssignableFrom(parameterType)) {

                        throw new IllegalArgumentException("Method called '" + method.getName() + "' " +
                                "declared in listeners class '" + listenersClass.getName() + "' is a listener method. " +
                                "If listener method has one parameter, its type must match at least one of following conditions: " +
                                "1. is an interface; 2. is a non-final class; 3. can be assigned to interface '" + Event.class.getName() + "'. ");
                    }

                    eventClasses = Collections.singleton(parameterType);
                } else {
                    throw new IllegalArgumentException("Method called '" + method.getName() + "' " +
                            "declared in listeners class '" + listenersClass.getName() + "', a listener method, has no parameter. " +
                            "If listener method has no parameter, its event classes declared in the field 'value' in annotation '@Listener' " +
                            "can not be empty, but it's. Make sure the method has 1 parameter or field 'value' in '@Listener' is not empty. ");
                }
            } else {
                if (parameters.length == 1) {

                    // if it has one parameter, parameter type must match at
                    // least one of following conditions:
                    //
                    // 1. is an interface,
                    // 2. is a non-final class,
                    // 3. can be assigned to event interface;

                    parameterType = parameters[0].getType();
                    final int parameterTypeModifiers = parameterType.getModifiers();

                    if (!Modifier.isInterface(parameterTypeModifiers)
                            && Modifier.isFinal(parameterTypeModifiers)
                            && !Event.class.isAssignableFrom(parameterType)) {

                        throw new IllegalArgumentException("Method declared in listeners class '" + listenersClass.getName() + "'" +
                                ", a listener method, has one parameter, whose type must match at least one of following conditions: " +
                                "1. is an interface; 2. is a non-final class; 3. can be assigned to interface '" + Event.class.getName() + "'. ");
                    }
                }

                final Set<Class<?>> eventClassesSet = new HashSet<>(Arrays.asList(eventClassesArray));

                // check if all event classes can be assigned to parameter type
                // if not, it's necessary to add parameter type to event class set
                boolean allEventClassesCanBeAssignedToParameterType = parameterType != null;

                for (int i = 0; i < eventClassesArray.length; i++) {
                    final Class<?> eventClass = eventClassesArray[i];

                    if (allEventClassesCanBeAssignedToParameterType
                            && !parameterType.isAssignableFrom(eventClass)) {
                        allEventClassesCanBeAssignedToParameterType = false;
                    }

                    final int eventClassModifiers = eventClass.getModifiers();

                    // check if instances of given event classes is impossible to be assigned to the parameter
                    // if it's impossible, it must be:
                    //
                    // 1. not an interface
                    // 2. final class and can not be assigned from event interface

                    // if it's an interface, can not judge if it's impossible
                    if (Modifier.isInterface(eventClassModifiers)) {
                        continue;
                    }

                    if (Modifier.isFinal(eventClassModifiers)) {
                        if (Event.class.isAssignableFrom(eventClass)) {
                            if (parameterType != null && !parameterType.isAssignableFrom(eventClass)) {

                                throw new IllegalArgumentException("Method called '" + method.getName() + "' " +
                                        "declared in listeners class '" + listenersClass.getName() + "'" +
                                        " is a listener method. The " + (i + 1) + " th value of field 'value' of " +
                                        "its annotation '@Listener' is '" + eventClass.getName() + "', which is impossible to be assigned " +
                                        "to its parameter type '" + parameterType.getName() + "'. Make sure it matches at least one of " +
                                        "following conditions: 1. is an interface; 2. is a non-final class; 3. can be assigned to interface " +
                                        "'" + Event.class.getName() + "'. ");
                            }
                        } else {
                            throw new IllegalArgumentException("Method called '" + method.getName() + "' " +
                                    "declared in listeners class '" + listenersClass.getName() + "'" +
                                    " is a listener method. The " + (i + 1) + " th value of field 'value' of " +
                                    "its annotation '@Listener' is '" + eventClass.getName() + "', which is impossible to implement " +
                                    "the event interface '" + Event.class.getName() + "'. Make sure it matches at least one of " +
                                    "following conditions: 1. is an interface; 2. is a non-final class; 3. can be assigned to interface " +
                                    "'" + Event.class.getName() + "'. ");
                        }
                    }
                }

                if (!allEventClassesCanBeAssignedToParameterType) {
                    eventClassesSet.add(parameterType);
                }
                eventClasses = Collections.unmodifiableSet(eventClassesSet);
            }

            final boolean ignoreCancelledEvent = listenerAnnotation.ignoreCancelledEvent();
            final Order order = listenerAnnotation.order();

            final Listener listener = new ReflectedListener(eventClasses, order, ignoreCancelledEvent, subject, listeners, method, parameterType);
            final List<Listener> sameOrderListeners = listenersToBeRegistered.computeIfAbsent(order, ignored -> new ArrayList<>());
            sameOrderListeners.add(listener);
        }

        listenersLock.writeLock().lock();
        try {
            for (Map.Entry<Order, List<Listener>> entry : listenersToBeRegistered.entrySet()) {
                this.listeners.computeIfAbsent(entry.getKey(), ignored -> new ArrayList<>()).addAll(entry.getValue());
            }
        } finally {
            listenersLock.writeLock().unlock();
        }
    }


}
