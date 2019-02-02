package com.coldradio.benzene.util;

import java.util.ArrayList;
import java.util.List;

public class StringSearchFilter<T> extends SearchFilter<T> {
    public StringSearchFilter(String keyword) {
        mKeyword = keyword;
    }

    @Override
    public List<T> filtered(List<T> list) {
        List<T> filteredList = new ArrayList<>();
        String lowerKeyword = mKeyword.toLowerCase();

        for (T index : list) {
            if (index.toString().toLowerCase().contains(lowerKeyword)) {
                filteredList.add(index);
            }
        }

        return filteredList;
    }
}
