package com.cjw.rxjavademo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cjw on 16-11-18.
 */

public class DateUtils {

    private DateUtils() {
    }

    public static String getSimpleYearMonthDay(long timeMillis) {
        return getDateString("yyyy-MM-dd", timeMillis);
    }

    public static String getSimpleHourMinuteSecondMillis(long timeMillis) {
        return getDateString("HH:mm:ss SSS", timeMillis);
    }

    public static String getSimpleHourMinuteSecond(long timeMillis) {
        return getDateString("HH:mm:ss", timeMillis);
    }

    public static String getDateString(String dateFormatStr, long timeMillis) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormatStr, Locale.CHINA);
        return df.format(new Date(timeMillis));
    }

}
