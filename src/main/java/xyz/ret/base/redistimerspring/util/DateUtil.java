package xyz.ret.base.redistimerspring.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String time(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }
}
