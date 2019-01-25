package com.coldradio.benzene.util;

import java.util.Locale;

public class TextUtil {
    public static String styleKeyword(String text, String keyword) {
        if (keyword == null || keyword.isEmpty())
            return text;

        StringBuilder sb = new StringBuilder();
        String lowerMsg = text.toLowerCase(Locale.ENGLISH);
        String lowerKeyword = keyword.toLowerCase(Locale.ENGLISH);

        for (int ii = 0, next; ii >= 0 && ii < text.length(); ) {
            next = lowerMsg.indexOf(lowerKeyword, ii);

            if (next < 0) {
                sb.append(text.substring(ii, text.length()));
                break;
            } else {
                sb.append(text.substring(ii, next));
                sb.append("<font color=black><b>");
                // add part of msg matched to keyword
                sb.append(text.substring(next, next + keyword.length()));
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

    public static int firstDigit(String text, int startIndex) {
        for (int ii = startIndex; ii < text.length(); ii++) {
            if (Character.isDigit(text.charAt(ii))) {
                return ii;
            }
        }
        return -1;
    }

    public static int firstNonDigit(String text, int startIndex) {
        for (int ii = startIndex; ii < text.length(); ii++) {
            if (! Character.isDigit(text.charAt(ii))) {
                return ii;
            }
        }
        return -1;
    }

    public static int[] firstInteger(String text, int startIndex) {
        int digitStart = firstDigit(text, startIndex);

        if (digitStart >= 0) {
            int digitEnd = firstNonDigit(text, digitStart);

            return new int[]{digitStart, digitEnd >= 0 ? digitEnd : text.length()};
        }
        return null;
    }

    public static String subscriptNumber(String text) {
        StringBuilder sb = new StringBuilder();

        for (int ii = 0; ii >= 0 && ii < text.length(); ) {
            int[] firstInteger = firstInteger(text, ii);

            if (firstInteger == null) {
                sb.append(text.substring(ii, text.length()));
                break;
            } else {
                sb.append(text.substring(ii, firstInteger[0]));
                sb.append("<sub><small><small>");
                // add part of msg matched to keyword
                sb.append(text.substring(firstInteger[0], firstInteger[1]));
                sb.append("</small></small></sub>");
                ii = firstInteger[1];
            }
        }
        return sb.toString();
    }
}
