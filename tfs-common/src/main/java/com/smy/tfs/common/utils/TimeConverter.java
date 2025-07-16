package com.smy.tfs.common.utils;

public class TimeConverter {
    public static String convertSecondsToDetailedTime(int seconds) {
        if (seconds == 0) {
            return "0秒";
        }

        seconds = Math.abs(seconds);
        int days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;

        StringBuilder timeDescription = new StringBuilder();
        if (days > 0) {
            timeDescription.append(days).append("天");
        }
        if (hours > 0) {
            timeDescription.append(hours).append("小时");
        }
        if (minutes > 0) {
            timeDescription.append(minutes).append("分");
        }
        if (seconds > 0 || timeDescription.length() == 0) {
            timeDescription.append(seconds).append("秒");
        }
        return timeDescription.toString();
    }
}
