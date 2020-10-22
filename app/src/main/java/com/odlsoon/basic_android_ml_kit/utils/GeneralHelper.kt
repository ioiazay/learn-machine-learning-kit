package com.odlsoon.basic_android_ml_kit.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object GeneralHelper {

    fun checkSinglePermission(activity: Activity, permission: String, requestCode: Int, callback: () -> Unit){
        if(ContextCompat.checkSelfPermission(activity.applicationContext, permission) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }else{
            callback()
        }
    }

    fun checkSinglePermissions(activity: Activity, permissions: Array<String>, requestCode: Int, callback: ()-> Unit){
        for(p in permissions){
            if(ContextCompat.checkSelfPermission(activity.applicationContext, p) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
            }else{
                callback()
                break
            }
        }
    }

}