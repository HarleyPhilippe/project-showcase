package com.example.sweetdreams

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SleepDetectionService : Service() {

    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SleepDetection::WakeLock")
        wakeLock.acquire()

        Log.d("SleepDetectionService", "Service created and wake lock acquired")

        // Simulate sleep detection
        detectSleep()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "SleepDetectionServiceChannel")
            .setContentTitle("Sleep Detection Service")
            .setContentText("Detecting sleep...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    private fun detectSleep() {
        CoroutineScope(Dispatchers.Default).launch {
            Log.d("SleepDetectionService", "Starting sleep detection")
            // Simulate sleep detection with a delay
            delay(10000) // Replace with actual detection logic
            withContext(Dispatchers.Main) {
                Log.d("SleepDetectionService", "Sleep detected, stopping streaming")
                stopStreaming()
            }
        }
    }

    private fun stopStreaming() {
        // Pause or stop the streaming service using its API
        // Example for a hypothetical streaming API
        // StreamingServiceAPI.pause()

        // Simulate putting the device to sleep by turning off the screen
        turnOffScreen()

        // Show message to user
        showMessage()
    }

    private fun turnOffScreen() {
        // This will just turn off the screen (requires WAKE_LOCK permission)
        if (wakeLock.isHeld) {
            wakeLock.release()
            Log.d("SleepDetectionService", "Wake lock released, screen should turn off")
        }
    }

    private fun showMessage() {
        Toast.makeText(this, "We've stopped your film, sweet dreams", Toast.LENGTH_LONG).show()
        Log.d("SleepDetectionService", "Toast message shown")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "SleepDetectionServiceChannel",
                "Sleep Detection Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
