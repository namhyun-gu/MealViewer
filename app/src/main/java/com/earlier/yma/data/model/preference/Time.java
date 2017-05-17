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
        return hour == another.hour && minute == another.getMinute();
    }
}
