package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.youtubePlayer.YouTubePlayersManager


class NotificationManager(private val context: Context, youTubePlayersManager: YouTubePlayersManager) : LifecycleObserver {
    private val notificationId = 1
    private val channelId = "CHANNEL_ID"

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "chromecast-youtube-player", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "sample-app"
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, thumbnail: Bitmap? = null) {
        val openActivityExplicitIntent = Intent(context.applicationContext, MainActivity::class.java)
        openActivityExplicitIntent.action = Intent.ACTION_MAIN
        openActivityExplicitIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(context.applicationContext, 0, openActivityExplicitIntent, 0)

        val togglePlaybackImplicitIntent = Intent(MyBroadcastReceiver.TOGGLE_PLAYBACK)
        val togglePlaybackPendingIntent = PendingIntent.getBroadcast(context, 0, togglePlaybackImplicitIntent, 0)

        val stopCastSessionImplicitIntent = Intent(MyBroadcastReceiver.STOP_CAST_SESSION)
        val stopCastSessionPendingIntent = PendingIntent.getBroadcast(context, 0, stopCastSessionImplicitIntent, 0)

        val notification = NotificationCompat.Builder(context, channelId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_audiotrack_dark)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_play_arrow_24dp, "Toggle Playback", togglePlaybackPendingIntent) // #0
                .addAction(R.drawable.ic_cast_connected_24dp, "Disconnect from chromecast", stopCastSessionPendingIntent)  // #1
                .setStyle(MediaStyle().setShowActionsInCompactView(0, 1))
                .setContentTitle(title)
                .setContentText("My Awesome Band")
//                .setAutoCancel(true)
                .setOngoing(true)
//                .setLargeIcon(thumbnail)
                .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notification)
    }

    fun dismissNotification() {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }
}