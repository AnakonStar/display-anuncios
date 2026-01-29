package com.enzo.llsant1.displayanuncios.modules.bootapp

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class BootService : Service() {

    private val TAG = "BOOT_APP_SERVICE"
    private val CHANNEL_ID = "BOOT_SERVICE_CHANNEL"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🔥 BootService onCreate chamado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "🚀 BootService onStartCommand chamado")

        startInForegroundSafely()

        return START_STICKY
    }

    private fun startInForegroundSafely() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = getSystemService(NotificationManager::class.java)

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Boot Service",
                    NotificationManager.IMPORTANCE_LOW
                )
                manager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Display Anúncios")
                .setContentText("Serviço iniciado após boot")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .build()

            startForeground(1, notification)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao iniciar foreground", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
