package com.tm00nlight.chi_homework_4

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var playMusicService: PlayMusicService? = null
    private val receiver = TrackProgressReceiver()
    private val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playMusicServiceIntent = Intent(this, PlayMusicService::class.java)

        val playButton = findViewById<Button>(R.id.playButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        playButton.setOnClickListener {
            startService(playMusicServiceIntent)

            bindService(playMusicServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        stopButton.setOnClickListener {
            stopService(playMusicServiceIntent)
            unbindService(serviceConnection)
            playMusicService = null
        }

        val progressBar = findViewById<SeekBar>(R.id.trackProgress)

        progressBar.setOnSeekBarChangeListener (object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playMusicService?.playerSeekTo(progress)
                }
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {
                Log.d("SEEK BAR", "OnStart")
            }

            override fun onStopTrackingTouch(sb: SeekBar?) {
                Log.d("SEEK BAR", "OnStop")
            }

        })

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
        unregisterReceiver(receiver)
        super.onPause()
    }

    private fun updateSeekBar(progress: Int) {
        val progressBar = findViewById<SeekBar>(R.id.trackProgress)
        progressBar.setProgress(progress, false)
    }

    inner class TrackProgressReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val progress: Int
            val action = intent?.action
            if(action.equals("TRACK_PROGRESS")){
                progress = intent?.getIntExtra("progress", 0) ?: 0
                Log.d("BROADCAST", "received $progress")
                updateSeekBar(progress)
            }
        }
    }
}