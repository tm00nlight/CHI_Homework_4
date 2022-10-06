package com.tm00nlight.chi_homework_4

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var playMusicService: PlayMusicService? = null
    private val receiver = TrackProgressReceiver()
    private val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playButton = findViewById<Button>(R.id.playButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        playButton.setOnClickListener {
            Intent(this, PlayMusicService::class.java).also { intent ->
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }

        stopButton.setOnClickListener {
            unbindService(serviceConnection)
            playMusicService = null
        }

        filter.addAction("TRACK_PROGRESS")
        registerReceiver(receiver, filter)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            playMusicService = (service as PlayMusicService.PlayMusicBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            playMusicService = null
        }
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