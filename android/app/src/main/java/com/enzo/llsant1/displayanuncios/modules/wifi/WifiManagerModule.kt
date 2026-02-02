package com.enzo.llsant1.displayanuncios.modules.wifi

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.enzo.llsant1.displayanuncios.MainActivity

class WifiManagerModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "WifiManager"

    @ReactMethod
    fun openWifiSettings() {
        val activity = reactApplicationContext.currentActivity ?: return
        activity.runOnUiThread {
            WifiHelper.openWifiSettings(activity)
        }
    }

    @ReactMethod
    fun promptWifiConnect() {
        val activity = reactApplicationContext.currentActivity as? MainActivity ?: return
        activity.runOnUiThread {
            activity.requestWifiPromptFromJs()
        }
    }

    @ReactMethod
    fun isInternetValidated(promise: Promise) {
        try {
            val ok = WifiHelper.isInternetValidated(reactApplicationContext)
            promise.resolve(ok)
        } catch (e: Exception) {
            promise.reject("wifi_check_failed", e)
        }
    }
}
