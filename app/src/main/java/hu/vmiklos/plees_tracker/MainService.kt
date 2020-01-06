/*
 * Copyright 2019 Miklos Vajna. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */

package hu.vmiklos.plees_tracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

import androidx.core.app.NotificationCompat

/**
 * A foreground service that just keeps the app alive, so the state is not lost
 * while tracking is on.
 */
class MainService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val dataModel = DataModel.dataModel
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val contentText = String.format(getString(R.string.sleeping_since),
                DataModel.formatTimestamp(dataModel.start!!))
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(NOTIFICATION_CODE, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    companion object {
        private val TAG = "MainService"
        private val NOTIFICATION_CHANNEL_ID = "Notification"
        private val NOTIFICATION_CODE = 1
    }
}
