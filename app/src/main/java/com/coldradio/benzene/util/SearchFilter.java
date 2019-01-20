package com.coldradio.benzene.util;

import java.util.List;
import java.util.Locale;

public abstract class SearchFilter<T> {
    protected String mKeyword;

    public String getKeyword() {
        return mKeyword;
    }

    public abstract List<T> filtered(List<T> list);
}