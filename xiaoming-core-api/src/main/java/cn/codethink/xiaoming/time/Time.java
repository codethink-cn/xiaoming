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

import cn.codethink.xiaoming.api.BotApiFactory;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <h1>Time</h1>
 *
 * @author Chuanwise
 */
public interface Time
    extends Comparable<Time> {

    /**
     * Returns a time represents now. 
     * 
     * @return time
     */
    static Time now() {
        return BotApiFactory.getBotApi().getTimeOfNow();
    }

    /**
     * Construct a time with provided seconds. 
     * 
     * @param seconds seconds
     * @return time
     * @throws IllegalArgumentException seconds is illegal
     */
    static Time ofSeconds(long seconds) {

    }

    /**
     * Construct a time with provided milliseconds. 
     *
     * @param milliseconds milliseconds
     * @return time
     * @throws IllegalArgumentException milliseconds is illegal
     */
    static Time ofMilliseconds(long milliseconds) {

    }

    /**
     * Convert time to seconds. 
     * 
     * @return seconds
     */
    long toSeconds();

    /**
     * Convert time to milliseconds. 
     *
     * @return milliseconds
     */
    long toMilliseconds();

    /**
     * Convert time to date. 
     *
     * @return date
     */
    Date toDate();

    /**
     * Convert time to calendar. 
     *
     * @return calender
     */
    Calendar toCalender();

    /**
     * Convert time to calendar with provided time zone. 
     *
     * @param timeZone time zone
     * @return calender
     * @throws NullPointerException time zone is null
     */
    Calendar toCalender(TimeZone timeZone);

    /**
     * Convert time to calendar with provided locale. 
     *
     * @param locale locale
     * @return calender
     * @throws NullPointerException locale is null
     */
    Calendar toCalender(Locale locale);

    /**
     * Convert time to calendar with provided time zone and locale. 
     *
     * @param timeZone time zone
     * @param locale locale
     * @return calender
     * @throws NullPointerException time zone or locale is null
     */
    Calendar toCalender(TimeZone timeZone, Locale locale);

    /**
     * Format time. 
     * 
     * @param dateFormat date format
     * @return formatted string
     * @throws NullPointerException date format is null
     */
    String format(DateFormat dateFormat);
}
