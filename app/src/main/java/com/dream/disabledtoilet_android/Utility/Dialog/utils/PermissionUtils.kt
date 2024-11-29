package com.dream.disabledtoilet_android.Utility.Dialog.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * 권한 요청 및 확인을 위한 유틸리티 클래스
 */
object PermissionUtils {

    /**
     * 권한 확인 및 요청
     */
    fun checkAndRequestPermission(activity: Activity, permission: String, requestCode: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            false
        } else {
            true
        }
    }
}
