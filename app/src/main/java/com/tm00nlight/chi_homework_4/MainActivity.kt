package com.tm00nlight.chi_homework_4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var playMusicService: Intent
    private val receiver = TrackProgressReceiver()
    private val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playMusicService =Intent(applicationContext, PlayMusicService::class.java)

        val playButton = findViewById<Button>(R.id.playButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        playButton.setOnClickListener {
            startService(playMusicService)
        }

        stopButton.setOnClickListener {
            stopService(playMusicService)
        }

        filter.addAction("TRACK_PROGRESS")
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun updateProgressBar(progress: Int) {
        val progressBar = findViewById<ProgressBar>(R.id.trackProgress)
        progressBar.setProgress(progress, false)
    }

    inner class TrackProgressReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val progress: Int
            val action = intent?.action
            if(action.equals("TRACK_PROGRESS")){
                progress = intent?.getIntExtra("progress", 0) ?: 0
                Log.d("BROADCAST", "received $progress")
                updateProgressBar(progress)
            }
        }
    }
}