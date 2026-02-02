package com.enzo.llsant1.displayanuncios

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.app.AlertDialog
import androidx.core.view.WindowCompat

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

import expo.modules.ReactActivityDelegateWrapper
import com.enzo.llsant1.displayanuncios.modules.immersive.ImmersiveModeHelper
import com.enzo.llsant1.displayanuncios.kiosk.KioskManager
import com.enzo.llsant1.displayanuncios.kiosk.WatchdogService
import com.enzo.llsant1.displayanuncios.modules.wifi.WifiHelper

class MainActivity : ReactActivity() {

  private lateinit var adminComponent: ComponentName

  override fun onCreate(savedInstanceState: Bundle?) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    setTheme(R.style.AppTheme)
    super.onCreate(null)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    ImmersiveModeHelper.enableStickyImmersive(this)
    ImmersiveModeHelper.enforcePersistentImmersive(this)

    adminComponent = ComponentName(this, DeviceAdminReceiver::class.java)
    KioskManager.enableKiosk(this, adminComponent)
    startWatchdog()
    ensureWifiConnected()
  }

  override fun onResume() {
    super.onResume()
    KioskManager.enableKiosk(this, adminComponent)
    ensureWifiConnected()
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "main"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate {
    return ReactActivityDelegateWrapper(
          this,
          BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
          object : DefaultReactActivityDelegate(
              this,
              mainComponentName,
              fabricEnabled
          ){})
  }

  /**
    * Align the back button behavior with Android S
    * where moving root activities to background instead of finishing activities.
    * @see <a href="https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
    */
  override fun invokeDefaultOnBackPressed() {
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
          if (!moveTaskToBack(false)) {
              // For non-root activities, use the default implementation to finish them.
              super.invokeDefaultOnBackPressed()
          }
          return
      }

      // Use the default back button implementation on Android S
      // because it's doing more than [Activity.moveTaskToBack] in fact.
      super.invokeDefaultOnBackPressed()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      ImmersiveModeHelper.enableStickyImmersive(this)
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      showExitDialog()
      return true
    }
    return super.onKeyDown(keyCode, event)
  }

  private fun showExitDialog() {
    val input = EditText(this)
    AlertDialog.Builder(this)
      .setTitle("Senha")
      .setView(input)
      .setPositiveButton("OK") { _, _ ->
        val ok = KioskManager.exitKiosk(this, input.text.toString())
        if (ok) {
          stopWatchdog()
          finishAndRemoveTask()
        }
      }
      .setNegativeButton("Cancelar", null)
      .show()
  }

  private fun ensureWifiConnected() {
    if (!WifiHelper.isConnectedToWifi(this)) {
      WifiHelper.openWifiSettings(this)
    }
  }

  private fun stopWatchdog() {
    val intent = Intent(this, WatchdogService::class.java)
    stopService(intent)
  }

  private fun startWatchdog() {
    val intent = Intent(this, WatchdogService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(intent)
    } else {
      startService(intent)
    }
  }
}
