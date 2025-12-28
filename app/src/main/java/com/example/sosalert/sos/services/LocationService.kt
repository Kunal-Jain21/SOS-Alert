package com.example.sosalert.sos.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sosalert.R
import com.example.sosalert.contact.data.EmergencyContactStore
import com.example.sosalert.utils.hasPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService() : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var smsSent = false

    companion object {
        const val TAG = "SSO Service"
        const val CHANNEL_ID = "sos_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification("Starting SOS..."))
        createLocationCallback()

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // ================= Notification ==========================
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SOS Service",
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    fun createNotification(
        text: String,
    ): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SOS Active")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    // ================= Location =============================

    fun createLocationCallback() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000)
            .setMinUpdateIntervalMillis(5_000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                handleLocation(location)
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(TAG, "Lost location permissions. Could not request updates. $exception")
        }
    }

    private fun handleLocation(location: Location) {
        val lat = location.latitude
        val lon = location.longitude

        // 1️⃣ Update notification
        updateNotification(
            lat = lat,
            lon = lon
        )

        // 2️⃣ Send location (example)
        if (!smsSent) {
            val message = "🚨 SOS ALERT 🚨\n" +
                    "I need help!\n" +
                    "My location:\n" +
                    "https://maps.google.com/?q=$lat,$lon"
            sendSmsToContacts(message = message)

        }
    }

    fun updateNotification(lat: Double, lon: Double) {
        val text = "Lat: $lat, Lon: $lon"
        val notification = createNotification(text)

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun sendSmsToContacts(message: String) {
        if (!hasPermission(Manifest.permission.SEND_SMS)) return

        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        EmergencyContactStore.contacts.forEach { contact ->
            val number = contact.number
                .replace("\\s".toRegex(), "")
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "")
                .trim()

            if (number.isNullOrBlank()) {
                Log.e(TAG, "Invalid phone number: ${contact.number}")
                return@forEach
            }

            try {
                smsManager.sendTextMessage(
                    number,
                    null,
                    message,
                    null,
                    null
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send SMS to $number", e)
            }
        }
    }
}