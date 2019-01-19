package com.coldradio.benzene.util;

import java.util.List;
import java.util.Locale;

public abstract class SearchFilter<T> {
    protected String mKeyword;

    public String styleKeyword(String msg) {
        if (mKeyword == null || mKeyword.isEmpty())
            return msg;

        StringBuilder sb = new StringBuilder();
        String lowerMsg = msg.toLowerCase(Locale.ENGLISH);
        String lowerKeyword = mKeyword.toLowerCase(Locale.ENGLISH);

        for (int ii = 0, next; ii >= 0 && ii < msg.length(); ) {
            next = lowerMsg.indexOf(lowerKeyword, ii);

            if (next < 0) {
                sb.append(msg.substring(ii, msg.length()));
                break;
            } else {
                sb.append(msg.substring(ii, next));
                sb.append("<font color=black><b>");
                // add part of msg matched to keyword
                sb.append(msg.substring(next, next + mKeyword.length()));
                sb.append("</b></font>");
                ii = next + mKeyword.length();
            }
        }
        return sb.toString();
    }

    public abstract List<T> filtered(List<T> list);
}