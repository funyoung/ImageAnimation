package com.funyoung.quickrepair.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ImageFragment extends Fragment {
    private View rootView;
    View.OnClickListener clickListener;


    String[] labels;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.image_fragment, container, false);
        rootView.setOnClickListener(clickListener);

        initMainCategory();
        return rootView;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private void initMainCategory() {
        ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
        labels = getResources().getStringArray(R.array.qp_category_label_array);
        for (int i = 0; i < 9; i++) {
            HashMap<String, Object> itemUnit = new HashMap<String, Object>();
            itemUnit.put("img", MainActivity.images[i]);
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
//        mMainId = i;
//        String[] subLabels = getResources().getStringArray(MainActivity.subLabelArray[mMainId]);
//        mSubCategory.clear();
//        mSubCategory.addAll(Arrays.asList(subLabels));
//        adapter.notifyDataSetChanged();
    }
}
