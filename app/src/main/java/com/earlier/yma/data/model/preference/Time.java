/*
 * Copyright 2016 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earlier.yma.data.model.preference;

public class Time implements Comparable<Time> {
    private int hour;
    private int minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public int compareTo(Time another) {
        if (hour > another.hour)
            return 1;
        else if (hour < another.hour)
            return -1;
        else if (hour == another.hour) {
            if (minute > another.minute)
                return 1;
            else if (minute < another.minute)
                return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        Time another = (Time) o;
        if (hour == another.hour && minute == another.getMinute())
            return true;
        return false;
    }
}
