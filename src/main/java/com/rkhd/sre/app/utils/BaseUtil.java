package com.rkhd.sre.app.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class BaseUtil {
    private static NumberFormat fmtD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.CHINA));
    private static NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.CHINA));

    public static String toDuration() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        double uptime = runtime.getUptime();
        uptime /= 1000;
        if (uptime < 60) {
            return fmtD.format(uptime) + "秒";
        }
        uptime /= 60;
        if (uptime < 60) {
            long minutes = (long) uptime;
            String s = fmtI.format(minutes) + "分";
            return s;
        }
        uptime /= 60;
        if (uptime < 24) {
            long hours = (long) uptime;
            long minutes = (long) ((uptime - hours) * 60);
            String s = fmtI.format(hours) + "小时";
            if (minutes != 0) {
                s += " " + fmtI.format(minutes) + "分";
            }
            return s;
        }
        uptime /= 24;
        long days = (long) uptime;
        long hours = (long) ((uptime - days) * 24);
        String s = fmtI.format(days) + "天";
        if (hours != 0) {
            s += " " + fmtI.format(hours) + "小时";
        }
        return s;
    }
}
