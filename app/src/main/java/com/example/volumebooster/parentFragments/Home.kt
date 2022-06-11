package com.example.volumebooster.parentFragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.media.audiofx.BassBoost
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.bullhead.equalizer.AnalogController
import com.example.volumebooster.R
import com.example.volumebooster.services.MusicServices
import me.tankery.lib.circularseekbar.CircularSeekBar
import kotlin.math.roundToInt


class Home : Fragment(), SeekBar.OnSeekBarChangeListener,
    CircularSeekBar.OnCircularSeekBarChangeListener {

    private lateinit var view1:View
    private var audioManager: AudioManager? = null
    private lateinit var analogController: AnalogController
    private lateinit var seekBar: SeekBar
    private lateinit var circularSeekBar: CircularSeekBar
    private lateinit var buttonMute: Button
    private lateinit var buttonMax: Button
    private lateinit var buttonThirty: Button
    private lateinit var buttonSixty: Button
    private lateinit var buttonHundred: Button
    private lateinit var buttonOneThirty: Button
    private var currentVol:Int = 0
    var pro:Float = 0.0F
    var boost:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view1 = inflater.inflate(R.layout.fragment_home, container, false)
        addView(view1)

        return view1
    }

    private fun addView(view: View) {
        audioManager = requireActivity().getSystemService(AUDIO_SERVICE) as AudioManager?

        buttonMax = view.findViewById(R.id.max)
        buttonMute = view.findViewById(R.id.mute)
        buttonThirty = view.findViewById(R.id.thirty)
        buttonSixty = view.findViewById(R.id.sixty)
        buttonHundred = view.findViewById(R.id.hund)
        buttonOneThirty = view.findViewById(R.id.one_thirty)
        circularSeekBar = view.findViewById(R.id.analog_controller)
        seekBar = view.findViewById(R.id.system_volume_seekbar)

        val maxVol = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        currentVol = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)

        //maxVol += 85
        seekBar.max = maxVol
        Log.d("MAX1", "addView: $maxVol")
        //seekBar.progress = currentVol

        buttonMax.setOnClickListener {
            seekBar.progress = 15
        }

        buttonMute.setOnClickListener {
            seekBar.progress = 0
        }

        buttonThirty.setOnClickListener {
            seekBar.progress = 4.5.roundToInt()

        }
        buttonSixty.setOnClickListener {
            seekBar.progress = 9
        }
        buttonHundred.setOnClickListener {
            seekBar.progress = 15
        }
        buttonOneThirty.setOnClickListener {
            seekBar.progress = 15

        }

        /*val vibration = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        analogController.setOnProgressChangedListener {
            if (Build.VERSION.SDK_INT >= 26) {
                vibration.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibration.vibrate(30)
            }
        }*/
        seekBar.setOnSeekBarChangeListener(this)
        circularSeekBar.setOnSeekBarChangeListener(this)

        val myBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("BR1", "onReceive: BroadcastReceiver is running")
                val ke = intent.getBundleExtra(Intent.EXTRA_KEY_EVENT) as KeyEvent
                if (ke .keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    val index: Int = seekBar.progress
                    seekBar.progress = index + 1
                }
                else if(ke .keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                {
                    val index: Int = seekBar.progress
                    seekBar.progress = index - 1
                }
            }
        }

    }

    //Horizontal Seekbar Methods
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, p1, 0)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }


    //Circular Seekbar Methods
    override fun onProgressChanged(circularSeekBar: CircularSeekBar?, progress: Float, fromUser: Boolean) {
        val vbService =requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (progress > 0.0) {
            vbService.vibrate(progress.toLong())
        }

        pro = progress
        circularSeekBar!!.rotation = progress - 100.0f
        listenBoosterProgress(progress)
    }

    override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
        if (pro.toInt() == 0) {
            circularSeekBar.rotation=240f
        }

    }

    override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
        if (pro.toInt() == 0) {
            circularSeekBar.rotation = 240f
        }
    }
    private fun listenBoosterProgress(progress: Float) {
        Log.d("boosterProgress", progress.toString())
        boost = progress.toInt()
        initVolumeBoosterService(boost)
    }
    private fun initVolumeBoosterService(boost:Int) {

        activity?.let { myactivity ->
            val myIntent = Intent(myactivity, MusicServices::class.java).apply {
                putExtra("volume_boost", boost)
                putExtra("app_volume", currentVol)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                myactivity.startForegroundService(myIntent)
            } else myactivity.startService(myIntent)

        }
    }

    /*fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mediaVlmSeekBar = findViewById(R.id.seekBar1) as SeekBar
            val index: Int = mediaVlmSeekBar.getProgress()
            mediaVlmSeekBar.setProgress(index + 1)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            val index: Int = mediaVlmSeekBar.getProgress()
            mediaVlmSeekBar.setProgress(index - 1)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }*/

}