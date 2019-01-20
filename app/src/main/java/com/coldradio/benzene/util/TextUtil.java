package com.coldradio.benzene.util;

import java.util.Locale;

public class TextUtil {
    public static String styleKeyword(String msg, String keyword) {
        if (keyword == null || keyword.isEmpty())
            return msg;

        StringBuilder sb = new StringBuilder();
        String lowerMsg = msg.toLowerCase(Locale.ENGLISH);
        String lowerKeyword = keyword.toLowerCase(Locale.ENGLISH);

        for (int ii = 0, next; ii >= 0 && ii < msg.length(); ) {
            next = lowerMsg.indexOf(lowerKeyword, ii);

            if (next < 0) {
                sb.append(msg.substring(ii, msg.length()));
                break;
            } else {
                sb.append(msg.substring(ii, next));
                sb.append("<font color=black><b>");
                // add part of msg matched to keyword
                sb.append(msg.substring(next, next + keyword.length()));
                sb.append("</b></font>");
                ii = next + keyword.length();
            }
        }
        return sb.toString();
    }

    public static String toCamelStyle(String text) {
        if (text.length() >= 2) {
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        } else {
            return text;
        }
    }
}
