package com.tm00nlight.chi_homework_4

import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val TAG = "PlayMusicService"
class PlayMusicService : Service() {
    private var progress = 0
    private var progress_ms = 0
    private val filter = IntentFilter()
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer.create(this, R.raw.hear_me) }
    private val binder = PlayMusicBinder()

    override fun onBind(intent: Intent): IBinder = binder

    fun playerSeekTo(progress: Int) {
        progress_ms = progress * mediaPlayer.duration / 100
        mediaPlayer.seekTo(progress_ms)
    }

    override fun onCreate() {
        filter.addAction("TRACK_PROGRESS")
        playMusic()
        showNotification()
        Log.d(TAG, "Service started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        stopMusic()
        Log.d(TAG, "Service stopped")
        super.onDestroy()
    }


    inner class PlayMusicBinder : Binder() {
        fun getService(): PlayMusicService = this@PlayMusicService
    }

    private fun playMusic() {
        mediaPlayer.start()
        val thread = Thread(Runnable {
            while (mediaPlayer.isPlaying) {
                progress = 100 * mediaPlayer.currentPosition / mediaPlayer.duration
                Log.d("THREAD", progress.toString())
                Intent().also { intent ->
                    intent.action = "TRACK_PROGRESS"
                    intent.putExtra("progress", progress)
                    sendBroadcast(intent)
                }
                Thread.sleep(1000)
            }
        })
        thread.start()
    }

    private fun stopMusic() {
        mediaPlayer.stop()
    }

    private fun showNotification() {
        val channelId = "audio_channel"
        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Audio Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_MUTABLE
                )
            }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Playing song")
            .setContentText("Imagine Dragons - Hear Me")
            .setSmallIcon(R.drawable.hear_me)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(500, notification)
    }

}