package com.himansh.oscontact;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by Himansh on 08-10-2017.
 */

public class Utility {
    public static void getAlertDialog(Context context, String title, String message, @Nullable DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(title);
        builder.setMessage(message);
        if(onClickListener==null) {
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        else{
            builder.setNeutralButton("OK",onClickListener);
        }
        builder.show();
    }
    public static boolean getPermission(final Context context, String action, String rationale, final int requestCode){
        if(ContextCompat.checkSelfPermission(context,action)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, READ_EXTERNAL_STORAGE)){
                getAlertDialog(context, "Permission Required : " + action, rationale, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity)context,new String[]{READ_EXTERNAL_STORAGE},requestCode);
                    }
                });
            }else {
                ActivityCompat.requestPermissions((Activity)context,new String[]{action},requestCode);
            }
            return false;
        }else{
            return true;
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        String s= Patterns.EMAIL_ADDRESS.pattern();
        Log.i("i",s);
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
