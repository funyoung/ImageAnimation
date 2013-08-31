package com.funyoung.quickrepair.model;

import android.text.TextUtils;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by yangfeng on 13-8-14.
 */
public class CategoryList extends ArrayList<Integer> {
    public static CategoryList parseListFromJson(String str) throws JSONException {
        CategoryList category = new CategoryList();
        if (!TextUtils.isEmpty(str)) {
            String[] idArray = str.split(",");
            for (String id : idArray) {
                category.add(Integer.parseInt(id));
            }
        }
        return category;
    }
}
