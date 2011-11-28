package com.thoughtworks.go.util;

import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

public class DateUtil {

    /**
     * Understands converting a string representation of a ISO 8601 time to long time from epoch.
     *
     * @param timeInISO8601 String date time in 8601 format.
     * @return time in millis from epoch for the given time.
     */
    public static long toDateInMillis(String timeInISO8601) {
        return toDate(timeInISO8601).getTime();
    }

    /**
     * Understands converting a string representation of a ISO 8601 time to a java.util.Date time from epoch.
     *
     * @param timeInISO8601 String date time in 8601 format.
     * @return java.util.Date object from epoch for the given time.
     */
    public static Date toDate(String timeInISO8601) {
        return ISODateTimeFormat.dateTimeNoMillis().parseDateTime(timeInISO8601).toDate();
    }
}
