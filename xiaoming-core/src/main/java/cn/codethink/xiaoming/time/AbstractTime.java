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

package cn.codethink.xiaoming.time;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class AbstractTime
    implements Time {

    private Date dateCache;
    private String toStringCache;
    private Integer hashCodeCache;

    @Override
    public final Date toDate() {
        if (dateCache == null) {
            dateCache = new Date(toMilliseconds());
        }
        return dateCache;
    }

    @Override
    public final Calendar toCalender() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(toMilliseconds());
        return calendar;
    }

    @Override
    public final Calendar toCalender(TimeZone timeZone) {
        Preconditions.checkNotNull(timeZone, "Time zone is null! ");

        final Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(toMilliseconds());
        return calendar;
    }

    @Override
    public final Calendar toCalender(Locale locale) {
        Preconditions.checkNotNull(locale, "Locale is null! ");

        final Calendar calendar = Calendar.getInstance(locale);
        calendar.setTimeInMillis(toMilliseconds());
        return calendar;
    }

    @Override
    public final Calendar toCalender(TimeZone timeZone, Locale locale) {
        Preconditions.checkNotNull(timeZone, "Time zone is null! ");
        Preconditions.checkNotNull(locale, "Locale is null! ");

        final Calendar calendar = Calendar.getInstance(timeZone, locale);
        calendar.setTimeInMillis(toMilliseconds());
        return calendar;
    }

    @Override
    public final String format(DateFormat dateFormat) {
        Preconditions.checkNotNull(dateFormat, "Date format is null! ");

        return dateFormat.format(toMilliseconds());
    }

    @Override
    public int compareTo(@NonNull Time o) {
        Preconditions.checkNotNull(o, "Time is null! ");

        return Long.compare(toMilliseconds(), o.toMilliseconds());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Time)) {
            return false;
        }
        final Time time = (Time) obj;
        return toMilliseconds() == time.toMilliseconds();
    }

    @Override
    public int hashCode() {
        if (hashCodeCache == null) {
            hashCodeCache = toDate().hashCode();
        }
        return hashCodeCache;
    }

    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = toDate().toString();
        }
        return toStringCache;
    }
}
