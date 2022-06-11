package com.example.volumebooster

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bullhead.equalizer.EqualizerFragment

class EqualizerActivity : AppCompatActivity() {

    var mediaPlayer= MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)

        try{
            val sessionId = mediaPlayer.audioSessionId
            val equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#6aefae"))
                .setAudioSessionId(sessionId)
                .build()
            supportFragmentManager.beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment)
                .commit()
            mediaPlayer.isLooping = true
        }
        catch (e:Exception)
        {

            Toast.makeText(this,"You must play a song first!", Toast.LENGTH_SHORT).show()
        }
    }


}