package com.enzo.llsant1.displayanuncios.modules.immersive

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat

object ImmersiveModeHelper {
    fun enableStickyImmersive(activity: Activity) {
        val window = activity.window ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = window.insetsController ?: return
            controller.hide(
                WindowInsets.Type.statusBars() or
                    WindowInsets.Type.navigationBars() or
                    WindowInsets.Type.systemGestures()
            )
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
        }
    }

    fun enforcePersistentImmersive(activity: Activity) {
        val window = activity.window ?: return
        val decor = window.decorView ?: return
        val handler = Handler(Looper.getMainLooper())
        val rehide = { enableStickyImmersive(activity) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decor.setOnApplyWindowInsetsListener { v, _ ->
                v.post { enableStickyImmersive(activity) }
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(rehide, 1500)
                WindowInsets.CONSUMED
            }
        } else {
            @Suppress("DEPRECATION")
            decor.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    decor.post { enableStickyImmersive(activity) }
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(rehide, 1500)
                }
            }
        }
    }
}
