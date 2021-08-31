package com.example.apnacr.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Helper {
    public  static ProgressDialog dialog;
    public static long getRandomNumber()
    {
        Random r = new Random();
        int i1 = r.nextInt(999999 - 100000) + 100000;
        Log.d("ClassNumber", "getRandomNumber: "+i1);
        return  i1;
    }

    public static String parseDateToddMMyyyy(Date date) {

        String outputPattern = "dd-MMM-yyyy";

        SimpleDateFormat format = new SimpleDateFormat(outputPattern);


        String str = "32-13-3000";

        try {

            str = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    public static void showProgressBar(Context context,String message)
    {
        dialog=new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();

    }
    public static void dismissDialog()
    {
        if(dialog!=null&&dialog.isShowing())
                dialog.dismiss();
    }

    public static boolean checkForPermision(Context context) {
        Activity activity=(Activity)context;
        if(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return true;

        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        return false;
    }
}
