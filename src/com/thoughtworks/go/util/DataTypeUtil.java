package com.thoughtworks.go.util;

import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

public class DataTypeUtil {
    public static Date toDate(String date) {
        return ISODateTimeFormat.dateTimeNoMillis().parseDateTime(date).toDate();
    }
}
