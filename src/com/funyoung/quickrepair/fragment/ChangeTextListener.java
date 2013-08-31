package com.funyoung.quickrepair.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.funyoung.qcwx.R;

/**
 * Created by yangfeng on 13-8-18.
 */
class ChangeTextListener implements View.OnClickListener {
    public interface OnChangeListener {
        public void onChangedFinish(View view, int keyResId, boolean cancel);
    }

    private View mView;
    private int mKeyResId;
    private OnChangeListener mChangeListener;

    public void setOnChangeListener(OnChangeListener l) {
        mChangeListener = l;
    }

    public ChangeTextListener (View view, int keyResId) {
        this(view, keyResId, null);
    }

    public ChangeTextListener (View view, int keyResId, OnChangeListener l) {
        mView = view;
        mKeyResId = keyResId;
        mChangeListener = l;
    }

    @Override
    public void onClick(View view) {
        final Context context = mView.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info).setTitle(mKeyResId);
        final EditText editText = new EditText(context);
        final TextView textView = (TextView)mView.findViewById(R.id.tv_content);
        editText.setText(textView.getText());
        editText.selectAll();
        builder.setView(editText);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textView.setText(editText.getText().toString());
                if (null != mChangeListener) {
                    mChangeListener.onChangedFinish(mView, mKeyResId, false);
                }
            }
        });
        builder.show();
    }
}
