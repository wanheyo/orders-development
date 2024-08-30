package com.prim.orders;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;

public class LoadingDialog {
    Activity activity;
    Fragment fragment;
    AlertDialog alertDialog;

     LoadingDialog(Activity myActivity) {
         activity = myActivity;
     }

     LoadingDialog(Fragment myFragment) {
         fragment = myFragment;
     }

     void startDialog() {
         AlertDialog.Builder builder = new AlertDialog.Builder(activity);

         LayoutInflater inflater = activity.getLayoutInflater();
         builder.setView(inflater.inflate(R.layout.loading_dialog, null));
         builder.setCancelable(false);

         alertDialog = builder.create();
         alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
         alertDialog.show();

     }

//    void startDialogInFragment() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(fragment);
//
//        LayoutInflater inflater = activity.getLayoutInflater();
//        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
//        builder.setCancelable(false);
//
//        alertDialog = builder.create();
//        alertDialog.show();;
//
//    }

     void dismissDialog() {
         alertDialog.dismiss();
     }
}
