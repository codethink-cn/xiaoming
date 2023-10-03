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

package cn.codethink.xiaoming.api;

import cn.codethink.xiaoming.Subject;
import cn.codethink.xiaoming.cause.Cause;
import cn.codethink.xiaoming.cause.DescriptionCause;
import cn.codethink.xiaoming.cause.ExceptionCause;
import cn.codethink.xiaoming.cause.ExceptionCauseImpl;
import cn.codethink.xiaoming.event.*;
import cn.codethink.xiaoming.id.*;
import cn.codethink.xiaoming.namespace.Namespace;
import cn.codethink.xiaoming.namespace.NamespaceImpl;
import cn.codethink.xiaoming.state.State;
import cn.codethink.xiaoming.state.StateImpl;
import cn.codethink.xiaoming.state.StateType;
import cn.codethink.xiaoming.time.MillisecondTime;
import cn.codethink.xiaoming.time.SecondTime;
import cn.codethink.xiaoming.time.Time;

public class BotApiImpl
    implements BotApi {

    @Override
    public LongId getLongId(long value) {
        return new LongIdImpl(value);
    }

    @Override
    public StringId getStringId(String value) {
        return new StringIdImpl(value);
    }

    @Override
    public Listener.Builder<?> getListenerBuilder() {
        return new FunctionalListener.BuilderImpl<>();
    }

    @Override
    public Time getTimeOfNow() {
        return new MillisecondTime(System.currentTimeMillis());
    }

    @Override
    public Time getTimeOfSeconds(long seconds) {
        return new SecondTime(seconds);
    }

    @Override
    public Time getTimeOfMilliseconds(long milliseconds) {
        return new MillisecondTime(milliseconds);
    }

    @Override
    public ExceptionCause getExceptionCause(Throwable exception, Time time) {
        return new ExceptionCauseImpl(exception, time);
    }

    @Override
    public Cause getDescriptionCause(String description) {
        return new DescriptionCause(description);
    }

    @Override
    public Cause getDescriptionCause(String description, Cause cause) {
        return new DescriptionCause(description, cause);
    }

    @Override
    public Namespace parseNamespace(String namespace) {
        return NamespaceImpl.parse(namespace);
    }

    @Override
    public NamespaceId parseNamespaceId(String namespaceId) {
        return NamespaceIdImpl.parse(namespaceId);
    }

    @Override
    public <T extends StateType> State<T> getState(T type, Cause cause) {
        return new StateImpl<>(type, cause);
    }

    @Override
    public CancellationStateOperation getCancellationStateOperation(boolean cancelled, Event event, Cause cause, Subject subject) {
        return new CancellationStateOperationImpl(cancelled, event, cause, subject);
    }

    @Override
    public InterceptionStateOperation getInterceptionStateOperation(boolean intercepted, Event event, Cause cause, Subject subject) {
        return new InterceptionStateOperationImpl(intercepted, event, cause, subject);
    }
}
