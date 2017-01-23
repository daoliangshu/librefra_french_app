package com.librefra.daoliangshu.librefra.daoliangboom.service;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by daoliangshu on 2016/10/29.
 */
public class MapComparator implements Comparator<HashMap<String, String>> {
    private final String key;

    public MapComparator(String key) {
        this.key = key;
    }

    public int compare(HashMap<String, String> first,
                       HashMap<String, String> second) {
        // TODO: Null checking, both for maps and values
        String firstValue = first.get(key);
        String secondValue = second.get(key);
        return firstValue.compareTo(secondValue);
    }
}