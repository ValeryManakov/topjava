package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;

public class MyDateTimeFormatter {
    private static MyDateTimeFormatter formatter;
    public static MyDateTimeFormatter getFormatter() {
        if (formatter == null) formatter = new MyDateTimeFormatter();
        return formatter;
    }

    private MyDateTimeFormatter() {
    }

    public String format(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;

        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();

        return String.format("%s-%s-%s %s:%s", intToString(year),
                intToString(month), intToString(day),
                intToString(hour), intToString(minute));
    }

    public String intToString(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    public LocalDateTime parse(String dateTime) {
        String[] numbers = dateTime.split("[-[ ]:]");
        return LocalDateTime.of(Integer.parseInt(numbers[0]),
                Integer.parseInt(numbers[1]),
                Integer.parseInt(numbers[2]),
                Integer.parseInt(numbers[3]),
                Integer.parseInt(numbers[4]));
    }
}
