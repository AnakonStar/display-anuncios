package com.enzo.llsant1.displayanuncios.modules.immersive

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class ImmersiveModeModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "ImmersiveMode"

    @ReactMethod
    fun enable() {
        val activity = reactApplicationContext.currentActivity ?: return
        activity.runOnUiThread {
            ImmersiveModeHelper.enableStickyImmersive(activity)
        }
    }
}
