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

package cn.codethink.xiaoming.cause;

import cn.codethink.xiaoming.time.Time;
import com.google.common.base.Preconditions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * <h1>Abstract Cause</h1>
 *
 * <p>Abstract cause is a simple implementation of {@link Cause} for
 * developers to inherit from. </p>
 *
 * <p>Not all causes must be inherited from this class, but if you are
 * developing a simple cause, it's recommended to inherit from it. </p>
 *
 * @see Cause
 * @author Chuanwise
 */
public abstract class AbstractCause
    implements Cause {

    /**
     * Direct cause.
     */
    private final Cause directCause;

    /**
     * Cache of direct cause.
     */
    private List<Cause> causesCache;

    /**
     * Time.
     */
    private final Time time;

    public AbstractCause(Cause directCause, Time time) {
        Preconditions.checkNotNull(directCause, "Direct cause is null! ");
        Preconditions.checkNotNull(time, "Time is null! ");

        this.directCause = directCause;
        this.time = time;
    }

    public AbstractCause(Time time) {
        Preconditions.checkNotNull(time, "Time is null! ");

        this.directCause = null;
        this.time = time;
    }

    public AbstractCause(Cause directCause) {
        Preconditions.checkNotNull(directCause, "Direct cause is null! ");

        this.directCause = directCause;
        this.time = Time.now();
    }

    public AbstractCause() {
        this.directCause = null;
        this.time = Time.now();
    }

    @Override
    public final Time getTime() {
        return time;
    }

    @Override
    public final Cause getDirectCause() {
        return directCause;
    }

    @Override
    public final Cause getDirectCauseOrFail() {
        if (directCause == null) {
            throw new NoSuchElementException("Direct cause isn't present!");
        }
        return directCause;
    }

    @Override
    public final List<Cause> getCauses() {
        if (causesCache == null) {
            if (directCause == null) {
                causesCache = Collections.emptyList();
            } else {
                final List<Cause> causes = new ArrayList<>();
                Cause cause = this.directCause;
                while (cause != null) {

                    causes.add(cause);
                    cause = cause.getDirectCause();

                    // check if the cause is returns to this
                    // if it's, loop causing is found.
                    if (cause == this) {
                        final StringBuilder stringBuilder = new StringBuilder("Loop causing detected! Cause chain: ");
                        stringBuilder.append(this);
                        for (Cause curr : causes) {
                            stringBuilder.append(" <- ").append(curr);
                        }
                        stringBuilder.append(" <- ").append(this).append(" (repeated) <- ...");
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
                causesCache = Collections.unmodifiableList(causes);
            }
        }
        return causesCache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Cause> T getCause(Class<T> causeClass) {
        Preconditions.checkNotNull(causeClass, "Cause class is null! ");

        for (Cause cause : getCauses()) {
            if (causeClass.isInstance(cause)) {
                return (T) cause;
            }
        }
        return null;
    }

    @Override
    public <T extends Cause> T getCauseOrFail(Class<T> causeClass) {
        final T cause = getCause(causeClass);
        if (cause == null) {
            throw new NoSuchElementException("Cause with provided class '" + causeClass.getName() + "' " +
                    "isn't present! ");
        }
        return cause;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cause)) {
            return false;
        }
        final Cause cause = (Cause) obj;
        return Objects.equals(directCause, cause.getDirectCause())
                && time.equals(cause.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(directCause);
    }

    @Override
    public void printCauseTrace() {
        printCauseTrace(System.out);
    }

    @Override
    public void printCauseTrace(PrintStream printStream) {
        Preconditions.checkNotNull(printStream, "Print stream is null! ");

        printCauseTrace(new PrintWriter(printStream));
    }

    @Override
    public void printCauseTrace(PrintWriter printWriter) {
        Preconditions.checkNotNull(printWriter, "Print writer is null! ");

        printWriter.printf("Cause '%s': %s (at '%s') has ", getClass().getName(), getDescription(), time);

        if (directCause == null) {
            printWriter.print("no any deeper cause. ");
        } else {
            final List<Cause> causes = getCauses();
            printWriter.print(causes.size());
            printWriter.println(causes.size() > 1 ? " deeper causes: " : " deeper cause: ");

            for (int i = 0; i < causes.size(); i++) {
                final Cause cause = causes.get(i);
                printWriter.printf("  (%d) '%s': %s (at '%s')\n", i + 1, cause.getClass().getName(), cause.getDescription(), cause.getTime());
            }
        }
    }

    @Override
    public String getCauseTrace() {
        final StringWriter stringWriter = new StringWriter();
        try (final PrintWriter printWriter = new PrintWriter(stringWriter, true)) {
            printCauseTrace(printWriter);
        }
        return stringWriter.getBuffer().toString();
    }

    @Override
    public String toString() {
        return "'" + getClass().getName() + "': " + getDescription();
    }
}
