package com.enzo.llsant1.displayanuncios.kiosk

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.UserManager
import android.util.Log

object KioskManager {
    private const val TAG = "KIOSK_MANAGER"
    private const val EXIT_PASSWORD = "1234" // TODO: customize your exit password

    fun enableKiosk(activity: Activity, admin: ComponentName) {
        val dpm = activity.getSystemService(DevicePolicyManager::class.java)

        if (!dpm.isDeviceOwnerApp(activity.packageName)) {
            Log.w(TAG, "App is not device owner; kiosk not enabled")
            return
        }

        dpm.setLockTaskPackages(admin, arrayOf(activity.packageName, "com.android.settings"))

        blockStatusBar(dpm, admin)
        disableKeyguard(dpm, admin)
        addSafeBootRestriction(dpm, admin)

        if (!isInLockTaskMode(activity)) {
            try {
                activity.startLockTask()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Unable to start lock task", e)
            }
        }
    }

    fun exitKiosk(activity: Activity, password: String): Boolean {
        if (password != EXIT_PASSWORD) return false

        val dpm = activity.getSystemService(DevicePolicyManager::class.java)
        val admin = ComponentName(activity, com.enzo.llsant1.displayanuncios.DeviceAdminReceiver::class.java)

        try {
            activity.stopLockTask()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "stopLockTask failed", e)
        }

        try {
            dpm.setStatusBarDisabled(admin, false)
        } catch (e: SecurityException) {
            Log.w(TAG, "Unable to re-enable status bar", e)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dpm.setKeyguardDisabled(admin, false)
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Unable to re-enable keyguard", e)
        }

        return true
    }

    private fun blockStatusBar(dpm: DevicePolicyManager, admin: ComponentName) {
        try {
            dpm.setStatusBarDisabled(admin, true)
        } catch (e: SecurityException) {
            Log.w(TAG, "setStatusBarDisabled failed", e)
        }
    }

    private fun disableKeyguard(dpm: DevicePolicyManager, admin: ComponentName) {
        try {
            dpm.setKeyguardDisabled(admin, true)
        } catch (e: SecurityException) {
            Log.w(TAG, "setKeyguardDisabled failed", e)
        }
    }

    private fun addSafeBootRestriction(dpm: DevicePolicyManager, admin: ComponentName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                dpm.addUserRestriction(admin, UserManager.DISALLOW_SAFE_BOOT)
            } catch (e: SecurityException) {
                Log.w(TAG, "addUserRestriction(DISALLOW_SAFE_BOOT) failed", e)
            }
        }
    }

    private fun isInLockTaskMode(activity: Activity): Boolean {
        val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return am.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }
}
