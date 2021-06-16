package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        if (startTime == null && endTime != null) return lt.compareTo(endTime) < 0;
        if (startTime != null && endTime == null) return lt.compareTo(startTime) >= 0;
        if (startTime == null && endTime == null) return true;
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
    }

    public static boolean isBetweenClosed(LocalDate ld, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate != null) return ld.compareTo(endDate) <= 0;
        if (startDate != null && endDate == null) return ld.compareTo(startDate) >= 0;
        if (startDate == null && endDate == null) return true;
        return ld.compareTo(startDate) >= 0 && ld.compareTo(endDate) <=0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}

