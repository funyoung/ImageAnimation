package com.funyoung.quickrepair.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yangfeng on 13-8-14.
 */
public class Rank {
    public static final String KEY_COUNT = "count";
    public static final String KEY_ONE_STAR = "star1";
    public static final String KEY_TWO_STAR = "star2";
    public static final String KEY_THREE_STAR = "star3";
    public static final String KEY_FOUR_STAR = "star4";
    public static final String KEY_FIVE_STAR = "star5";

    public int mCount;
    public ArrayList<Integer> mMarkList;

    public static Rank parseFromJson(String str) throws JSONException {
        Rank rank = new Rank();
        if (null != str) {
            JSONObject jsonObject = new JSONObject(str);
            rank.mCount = jsonObject.optInt(KEY_COUNT);
            rank.mMarkList = new ArrayList<Integer>(5);
            rank.mMarkList.add(jsonObject.optInt(KEY_ONE_STAR));
            rank.mMarkList.add(jsonObject.optInt(KEY_TWO_STAR));
            rank.mMarkList.add(jsonObject.optInt(KEY_THREE_STAR));
            rank.mMarkList.add(jsonObject.optInt(KEY_FOUR_STAR));
            rank.mMarkList.add(jsonObject.optInt(KEY_FIVE_STAR));
        }
        return rank;
    }
}
