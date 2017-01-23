package com.librefra.daoliangshu.librefra.tools;

import java.util.HashMap;

/**
 * Created by daoliangshu on 11/27/16.
 * Store the categories of the vocabularies
 * Currently not used
 */

public class CategoryManager {
    public static HashMap<String, String[]> categoryMap;

    public static void initCategoryMap() {
        categoryMap.put("no", new String[]{"aucune", "無"});
        categoryMap.put("nature", new String[]{"nature", "自然"});
        categoryMap.put("food", new String[]{"nourriture", "食物"});
        categoryMap.put("person", new String[]{"personne", "人"});
        categoryMap.put("economy", new String[]{"économie", "經濟"});
        categoryMap.put("feeling", new String[]{"sentiment", "感覺"});
        categoryMap.put("time", new String[]{"temporalité", "時間"});
        categoryMap.put("animal", new String[]{"animal", "動物"});
        categoryMap.put("body", new String[]{"corps", "身體"});
        categoryMap.put("family", new String[]{"famille", "家庭"});
    }
}
