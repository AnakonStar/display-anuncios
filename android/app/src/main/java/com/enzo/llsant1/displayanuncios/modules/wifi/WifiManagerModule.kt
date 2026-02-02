package com.enzo.llsant1.displayanuncios.modules.wifi

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

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
}
