package com.funyoung.quickrepair.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by yangfeng on 13-8-10.
 */

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public  class CategoryGridFragment extends BaseFragment {
    private View rootView;

    String[] labels;
    private ArrayList<String> mSubCategory = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private int mMainId = 0;

    public CategoryGridFragment() {
        // Empty constructor required for fragment subclasses
    }

    private static int[] subIdArray = {
            R.array.qp_category_id_array_sub1,
            R.array.qp_category_id_array_sub2,
            R.array.qp_category_id_array_sub3,
            R.array.qp_category_id_array_sub4,
            R.array.qp_category_id_array_sub5,
            R.array.qp_category_id_array_sub6,
            R.array.qp_category_id_array_sub7,
            R.array.qp_category_id_array_sub8,
            R.array.qp_category_id_array_sub9,
    };

    private static int[] subLabelArray = {
            R.array.qp_category_label_array_sub1,
            R.array.qp_category_label_array_sub2,
            R.array.qp_category_label_array_sub3,
            R.array.qp_category_label_array_sub4,
            R.array.qp_category_label_array_sub5,
            R.array.qp_category_label_array_sub6,
            R.array.qp_category_label_array_sub7,
            R.array.qp_category_label_array_sub8,
            R.array.qp_category_label_array_sub9,
    };

    public static Integer[] images = {
            R.drawable.ic_classify_airconditioning,
            R.drawable.ic_classify_heater,
            R.drawable.ic_classify_closestool,
            R.drawable.ic_classify_electric,
            R.drawable.ic_classify_pipeline,
            R.drawable.ic_classify_appliance,
            R.drawable.ic_classify_house,
            R.drawable.ic_classify_furniture,
            R.drawable.ic_classify_more
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category, container, false);

        initMainCategory();
        initSubCategory();

        return rootView;
    }

    private void initSubCategory() {
        String[] subLabels = getResources().getStringArray(subLabelArray[mMainId]);
        mSubCategory.clear();
        mSubCategory.addAll(Arrays.asList(subLabels));

        GridView gridView = (GridView) rootView.findViewById(R.id.ptr_gridview_sub);
        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.gridview_item_category_sub,
                mSubCategory);

//        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
//                itemData,
//                R.layout.gridview_item_category,
//                new String[] { "img", "label" },
//                new int[] { R.id.img, R.id.label });

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int[] subIds = getResources().getIntArray(subIdArray[mMainId]);
                ((MainActivity)getActivity()).startPost(mMainId + 1, subIds[i], labels[mMainId], mSubCategory.get(i));
            }
        });
    }

    private void initMainCategory() {
        ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
        labels = getResources().getStringArray(R.array.qp_category_label_array);
        for (int i = 0; i < 9; i++) {
            HashMap<String, Object> itemUnit = new HashMap<String, Object>();
            itemUnit.put("img", images[i]);
            itemUnit.put("label", labels[i]);
            itemData.add(itemUnit);
        }

        GridView gridView = (GridView) rootView.findViewById(R.id.ptr_gridview);
//        ListAdapter adapter = new ArrayAdapter<String>(getActivity(),
//                R.layout.gridview_item_category,
//                labels);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                itemData,
                R.layout.gridview_item_category,
                new String[] { "img", "label" },
                new int[] { R.id.img, R.id.label });

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showSubCategory(i);
//                ((MainActivity)getActivity()).startPost(i, labels[i]);
            }
        });
    }

    private void showSubCategory(int i) {
        mMainId = i;
        String[] subLabels = getResources().getStringArray(subLabelArray[mMainId]);
        mSubCategory.clear();
        mSubCategory.addAll(Arrays.asList(subLabels));
        adapter.notifyDataSetChanged();
//        ((MainActivity)getActivity()).startPost(i, labels[mMainId]);
    }
}

