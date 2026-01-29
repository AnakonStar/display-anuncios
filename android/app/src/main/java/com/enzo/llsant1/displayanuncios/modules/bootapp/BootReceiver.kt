package com.enzo.llsant1.displayanuncios.modules.bootapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.enzo.llsant1.displayanuncios.MainActivity

class BootReceiver : BroadcastReceiver() {

    private val TAG = "BOOT_APP_RECEIVER"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Recebido: ${intent.action}")

        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_USER_UNLOCKED) {
            val intentActivity = Intent(context, MainActivity::class.java)
            intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intentActivity)
        }
    }
}
