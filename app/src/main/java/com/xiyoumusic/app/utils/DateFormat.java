package com.xiyoumusic.app.utils;

import java.text.SimpleDateFormat;

public class DateFormat {
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM = "yyyy-MM-dd";

    public static String getDateFromID(Long id ,String format){
        try {
            Long id1 = id >> 12;
            SimpleDateFormat dateformat = new SimpleDateFormat(format);
            return dateformat.format(id1);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
