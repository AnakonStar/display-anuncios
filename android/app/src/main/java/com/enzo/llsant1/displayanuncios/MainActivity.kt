package com.enzo.llsant1.displayanuncios

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
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
  private var connectivityManager: ConnectivityManager? = null
  private var networkCallback: ConnectivityManager.NetworkCallback? = null
  private var waitingForWifi = false
  private var hasShownConnectPrompt = false
  private var stoppedWatchdogForWifi = false
  private var navigatingToWifiSettings = false

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
    connectivityManager = getSystemService(ConnectivityManager::class.java)
    KioskManager.enableKiosk(this, adminComponent)
    startWatchdog()
    ensureWifiConnected()
  }

  override fun onResume() {
    super.onResume()
    KioskManager.enableKiosk(this, adminComponent)
    if (stoppedWatchdogForWifi) {
      startWatchdog()
      stoppedWatchdogForWifi = false
    }
    if (navigatingToWifiSettings) {
      navigatingToWifiSettings = false
    }
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

  override fun onBackPressed() {
    // Ignore back to avoid leaving the kiosk accidentally; exit is gated by the password flow.
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
    if (WifiHelper.isConnectedToWifi(this)) {
      hasShownConnectPrompt = false
      return
    }

    promptOpenWifiSettings()
    startWaitingForWifi()
  }

  fun requestWifiPromptFromJs() {
    // Allow showing the prompt again when explicitly requested by the JS retry button.
    hasShownConnectPrompt = false
    ensureWifiConnected()
  }

  private fun promptOpenWifiSettings() {
    if (hasShownConnectPrompt) return
    hasShownConnectPrompt = true
    AlertDialog.Builder(this)
      .setTitle("Conectar")
      .setMessage("É necessário uma conexão com a internet para prosseguir.")
      .setPositiveButton("Conectar") { _, _ ->
        stoppedWatchdogForWifi = true
        navigatingToWifiSettings = true
        stopWatchdog()
        WifiHelper.openWifiSettings(this)
      }
      .setNegativeButton("Cancelar", null)
      .show()
  }

  private fun startWaitingForWifi() {
    if (waitingForWifi) return
    waitingForWifi = true

    val request = NetworkRequest.Builder()
      .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build()

    val callback = object : ConnectivityManager.NetworkCallback() {
      override fun onAvailable(network: Network) {
        evaluateNetwork(network)
      }

      override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        evaluateNetwork(network)
      }
    }

    connectivityManager?.registerNetworkCallback(request, callback)
    networkCallback = callback
  }

  private fun evaluateNetwork(network: Network) {
    val cm = connectivityManager ?: return
    val capabilities = cm.getNetworkCapabilities(network) ?: return
    val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    if (isValidated) {
      runOnUiThread { handleStableConnection() }
    }
  }

  private fun handleStableConnection() {
    if (!waitingForWifi) return
    waitingForWifi = false
    hasShownConnectPrompt = false
    unregisterNetworkCallback()
    restartApp()
  }

  private fun unregisterNetworkCallback() {
    val callback = networkCallback ?: return
    connectivityManager?.unregisterNetworkCallback(callback)
    networkCallback = null
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

  private fun restartApp() {
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return
    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(launchIntent)
    finish()
  }

  override fun onDestroy() {
    unregisterNetworkCallback()
    super.onDestroy()
  }
}
