package com.suyati.mapstrackingcurrentlocationfinal.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by suyati on 2/28/17.
 */

public class DateTimeUtility {

    public static Date getOnlyTimeHour(String timeString) throws ParseException {
        String pattern = "dd/MM/yyyy HH:mm:ss";
        String timepattern = "HH";
        DateFormat originalFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat(timepattern);
        Date date = originalFormat.parse(timeString);
        Date formattedDate = targetFormat.parse(targetFormat.format(date));
        return  formattedDate;
    }
}
