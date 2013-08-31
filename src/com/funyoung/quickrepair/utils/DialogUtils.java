package com.funyoung.quickrepair.utils;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListAdapter;

public class DialogUtils {
    private static final String TAG = "DialogUtils";

    public static ProgressDialog createProgressDialog(final Context context, int resid,
                                                      boolean CanceledOnTouchOutside, boolean Indeterminate, boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(resid));
        dialog.setCanceledOnTouchOutside(CanceledOnTouchOutside);
        dialog.setIndeterminate(Indeterminate);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    public static ProgressDialog createProgressDialogWithTitle(final Context context, int resid, int resTitle,
            boolean CanceledOnTouchOutside, boolean Indeterminate, boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(resTitle);
        dialog.setMessage(context.getString(resid));
        dialog.setCanceledOnTouchOutside(CanceledOnTouchOutside);
        dialog.setIndeterminate(Indeterminate);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    public static void showConfirmDialog(final Context context, int resTitle, int resMsg, int resOk, int resCancel,
                                         DialogInterface.OnClickListener listener) {
        showConfirmDialog(context, resTitle, resMsg, resOk, resCancel, listener, null);
    }

    public static void showConfirmDialog(final Context context, int resTitle, int resMsg, int resOk, int resCancel,
                                         DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder replaceBuilder = new AlertDialog.Builder(context);
        replaceBuilder.setTitle(resTitle)
                .setMessage(resMsg)
                .setPositiveButton(resOk, positiveListener)
                .setNegativeButton(resCancel, negativeListener);
        AlertDialog dialog = replaceBuilder.create();

        dialog.show();
    }

    public static void showConfirmDialog(final Context context, String resTitle, String resMsg,
                                         DialogInterface.OnClickListener listener) {
        showConfirmDialog(context, resTitle, resMsg, listener, null);
    }

    public static AlertDialog showConfirmDialog(final Context context, String resTitle, String resMsg,
                                         DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder replaceBuilder = new AlertDialog.Builder(context);
        replaceBuilder.setTitle(resTitle)
                .setMessage(resMsg)
                .setPositiveButton(R.string.ok, positiveListener)
                .setNegativeButton(R.string.cancel, negativeListener);
        AlertDialog dialog = replaceBuilder.create();
        
        dialog.show();
        return dialog;
    }

    public static void showSingleChoiceDialog(final Context context, int resTitle, final String[] items, final int checkItem,
                                              DialogInterface.OnClickListener itemClickListener,
                                              DialogInterface.OnClickListener positiveListener,
                                              DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder replaceBuilder = new AlertDialog.Builder(context);
        replaceBuilder.setTitle(resTitle)
                .setSingleChoiceItems(items, checkItem, itemClickListener);
        if (positiveListener != null) {
            replaceBuilder.setPositiveButton(R.string.ok, positiveListener);
        }
        if (negativeListener != null) {
            replaceBuilder.setNegativeButton(R.string.cancel, negativeListener);
        }
        AlertDialog dialog = replaceBuilder.create();

        dialog.show();
    }
    
    public static AlertDialog showSingleChoiceDialogWithAdapter(final Context context, int resTitle, final ListAdapter adapter, final int checkedItem,
    		DialogInterface.OnClickListener itemClickListener,
    		DialogInterface.OnClickListener positiveListener,
    		DialogInterface.OnClickListener negativeListener) {
    	AlertDialog.Builder replaceBuilder = new AlertDialog.Builder(context);
    	replaceBuilder.setTitle(resTitle).setSingleChoiceItems(adapter, checkedItem, itemClickListener);
//    	.setSingleChoiceItems(items, checkItem, itemClickListener);
    	if (positiveListener != null) {
    		replaceBuilder.setPositiveButton(R.string.ok, positiveListener);
    	}
    	if (negativeListener != null) {
    		replaceBuilder.setNegativeButton(R.string.cancel, negativeListener);
    	}
    	AlertDialog dialog = replaceBuilder.create();
    	
    	dialog.show();
    	
    	return dialog;
    }


    public static void showItemsDialog(final Context context, String resTitle, int resIcon, final String[] items,
                                       DialogInterface.OnClickListener ChooseItemClickListener) {
        AlertDialog.Builder itemsBuilder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(resTitle)) {
            itemsBuilder.setTitle(resTitle);
        }
        if (resIcon > 0) {
            itemsBuilder.setIcon(resIcon);
        }
        itemsBuilder.setItems(items, ChooseItemClickListener);
        itemsBuilder.create().show();
    }

    public static void showOKDialog(final Context context, int resTitle,
                                    int resMsg, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (resTitle > 0) {
            builder.setTitle(resTitle);
        }
        builder.setMessage(resMsg)
                .setPositiveButton(R.string.ok, positiveListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static AlertDialog ShowDialogwithView(Context context, String title, int iconRes, View view,
                                                 DialogInterface.OnClickListener positiveListener,
                                                 DialogInterface.OnClickListener negativeListener) {

        AlertDialog.Builder Dialogbuilder = new AlertDialog.Builder(context);
        Dialogbuilder.setTitle(title);
        Dialogbuilder.setView(view);
        if (iconRes > 0) {
            Dialogbuilder.setIcon(iconRes);
        }

        if (positiveListener != null) {
            Dialogbuilder.setPositiveButton(R.string.ok, positiveListener);
        }
        if (negativeListener != null) {
            Dialogbuilder.setNegativeButton(R.string.cancel, negativeListener);
        }

        AlertDialog viewDialog = Dialogbuilder.create();
        viewDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        viewDialog.show();
        return viewDialog;
    }

    public static AlertDialog ShowDialogwithView(Context context, int resTitle, View view,
            int resLeft, int resRight,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(resTitle);
        builder.setView(view);

        if (positiveListener != null) {
            builder.setPositiveButton(resLeft, positiveListener);
        }
        if (negativeListener != null) {
            builder.setNegativeButton(resRight, negativeListener);
        }

        AlertDialog viewDialog = builder.create();
        viewDialog.show();
        return viewDialog;
    }
    
    public static void showRetryDialog(Context context, int resTitle, int resMsg, int resPos, int resNeu,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener neutralListener) {
        String title = context.getResources().getString(resTitle);
        String msg = context.getResources().getString(resMsg);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(msg)
                .setPositiveButton(resPos, positiveListener)
                .setNeutralButton(resNeu, neutralListener)
                .setNegativeButton(R.string.cancel, null);
//                .setNegativeButton(textId, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//    public void showListDialog(final Context context, String title) {
//        AlertDialog.Builder replaceBuilder = new AlertDialog.Builder(context);
//        replaceBuilder.setTitle(resTitle)
//                .setMessage(resMsg)
//                .setPositiveButton(R.string.label_ok, positiveListener)
//                .setNegativeButton(R.string.label_cancel, negativeListener);
//        AlertDialog dialog = replaceBuilder.create();
//
//        dialog.show();
//    }

    public static Dialog showProgressDialog(Context context, int titleRes, String msgRes) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(titleRes);
        dialog.setMessage(msgRes);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    public static interface PhotoPickInterface {
        public void doTakePhotoCallback(); // from camera
        public void doPickPhotoFromGalleryCallback(); // from  gallery
    }

//    public static AlertDialog ShowPhotoPickDialog(Context context, final PhotoPickInterface callBack) {
//        String[] items = new String[]{context.getString(R.string.edit_profile_img_camera),
//                context.getString(R.string.edit_profile_img_location)};
//        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            dialog.dismiss();
//                            callBack.doTakePhotoCallback();
//                        } else {
//                            dialog.dismiss();
//                            callBack.doPickPhotoFromGalleryCallback();
//                        }
//                    }
//                });
//
//            AlertDialog viewDialog = builder.create();
//            viewDialog.show();
//            return viewDialog;
//        }
}
