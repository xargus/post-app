package center.xargus.postapp.utils;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatUtils {
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentTime() {
        Date d = new Date();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        String currentTime = df.format(d);

        return currentTime;
    }

    public static boolean validateFormat(String date, String format) {
        boolean result = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            simpleDateFormat.parse(date);
            result = true;
        } catch (ParseException e) {
            Logger.getLogger(TimeFormatUtils.class).warn(e.getMessage());
        }

        return result;
    }
}
