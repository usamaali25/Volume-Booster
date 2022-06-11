package com.example.volumebooster

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.volumebooster.databinding.ActivityArtistsMusicBinding
import com.example.volumebooster.interfaces.ActionPlaying
import com.example.volumebooster.notification.NotificationReceiver
import com.example.volumebooster.parentFragments.childFragments.Artists
import com.example.volumebooster.services.ArtistsMusicServices
import com.example.volumebooster.services.MusicServices
import java.io.IOException

class ArtistsMusicActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, ActionPlaying,
    ServiceConnection {

    companion object{
        var myMusicService: ArtistsMusicServices? = null
    }

    private lateinit var binding: ActivityArtistsMusicBinding
    private lateinit var audioList: MutableList<AudioModel>
    //private var mPlayer: MediaPlayer? = null
    private lateinit var paths:String
    lateinit var uri: Uri
    private lateinit var audioManager: AudioManager
    var position = -1
    lateinit var mediaSessionCompat: MediaSessionCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistsMusicBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        mediaSessionCompat = MediaSessionCompat(baseContext,"My Audio")
        getIntentMethod()
        binding.songName.text = audioList[position].getaName()
        binding.back.setOnClickListener {
            onBackPressed()
        }
        var b = false //Boolean value
        binding.start.setOnClickListener {
            if(b == false)
            {
                startPlaying(uri)
                showNotification()
                binding.start.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                b= true
            }
            else
            {
                pausePlaying()
                showNotification()
                binding.start.setImageResource(R.drawable.ic_baseline_pause_24)
                b=false
            }

        }

        binding.seekbar.setOnSeekBarChangeListener(this)

    }


    override fun onResume() {
        super.onResume()
        val intent = Intent(this, MusicServices::class.java)
        startService(intent)
        bindService(intent,this, BIND_AUTO_CREATE)
        nextThreadButton()
        prevThreadButton()
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }


    private fun prevThreadButton() {
        binding.rewind.setOnClickListener {
            backBtnClicked()
        }
    }

    override fun backBtnClicked() {
        if (myMusicService!!.isPlaying())
        {
            myMusicService!!.stop()
            myMusicService!!.release()
            Log.d("POS1", "backBtnClicked: $position")
            if (position == 0)
            {
                position = audioList.size - 1
            }
            else
            {
                position = ((position - 1) % audioList.size)
            }
            showNotification()
            uri = Uri.parse(audioList[position].getaPath())
            //mPlayer = MediaPlayer.create(this,uri)
            myMusicService!!.createMediaPlayer(position)
            metaData(uri)
            binding.songName.text = audioList[position].aName
            var b = false //Boolean value
            startPlaying(uri)
            binding.start.setOnClickListener {
                if(b == false)
                {
                    startPlaying(uri)
                    showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    b= true
                }
                else
                {
                    pausePlaying()
                    showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_pause_24)
                    b=false
                }

            }
        }
    }

    private fun nextThreadButton() {

        binding.forward.setOnClickListener{
            nextBtnClicked()
        }
    }

    override fun nextBtnClicked() {
        if (myMusicService!!.isPlaying())
        {
            myMusicService!!.stop()
            myMusicService!!.release()
            position = ((position + 1) % audioList.size)
            uri = Uri.parse(audioList[position].getaPath())
            myMusicService!!.createMediaPlayer(position)
            metaData(uri)
            binding.songName.text = audioList[position].aName
            var b = false //Boolean value
            startPlaying(uri)
            binding.start.setOnClickListener {
                if(b == false)
                {
                    startPlaying(uri)
                    showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    b= true
                }
                else
                {
                    pausePlaying()
                    showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_pause_24)
                    b=false
                }

            }
        }
    }

    fun getIntentMethod()
    {
        position = intent.getIntExtra("position",-1)
        Log.d("POS2", "getIntentMethod: $position")
        audioList = Artists.tempAudioList
        if (audioList.isNotEmpty())
        {
            uri = Uri.parse(audioList[position].getaPath())
        }
        val intent = Intent(this, MusicServices::class.java)
        intent.putExtra("position",position)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }
    }
    fun metaData(uri: Uri)
    {
        val mr = MediaMetadataRetriever()
        mr.setDataSource(uri.toString())
        val dur:Int = Integer.parseInt(audioList[position].getaDuration().toString())
        binding.totalTime.text = DateUtils.formatElapsedTime((dur/1000).toLong())
        val art = mr.embeddedPicture
        if(art!=null)
        {
            Glide.with(this)
                .asBitmap()
                .load(art)
                .into(binding.thumbnail)
        }
        else
        {
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.musical_note)
                .into(binding.thumbnail)
        }
    }
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if(p2) {
            myMusicService?.seekTo(p1)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    /**
     *
     *
    StartPlaying Method
     *
     *
     */
    override fun startPlaying(uri: Uri) {
        if (myMusicService != null && myMusicService!!.isPlaying()) {
            initialiseSeekBar()
            myMusicService!!.pause()

        } else if (myMusicService != null) {
            myMusicService!!.start()
        } else {
            myMusicService = ArtistsMusicServices()
            try {
                myMusicService!!.setDataSource(uri)
                myMusicService!!.prepare()
                myMusicService!!.start()
                initialiseSeekBar()
            } catch (e: IOException) {
                Log.d("LOG_TAG", "prepare() failed")
            }
        }
    }
    /**
     *
     *
    initialiseSeekBar Method
     *
     *
     */
    private fun initialiseSeekBar() {
        binding.seekbar.max = myMusicService!!.getDuration()
        val handler = Handler()
        handler.postDelayed(object : Runnable{
            override fun run() {
                try {
                    binding.seekbar.progress = myMusicService!!.currentPosition()
                    val currentDuration = myMusicService!!.currentPosition()
                    binding.runTime.text = DateUtils.formatElapsedTime((currentDuration/1000).toLong())
                    handler.postDelayed(this,0)

                }catch (e:Exception)
                {
                    binding.seekbar.progress = 0
                }
            }
        },0)
    }

    /**
     *
    PausePlaying Method
     *
     */

    override fun pausePlaying() {
        if (myMusicService!!.isPlaying()) {
            myMusicService!!.pause()
        } else {
            myMusicService!!.start()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binders = service as ArtistsMusicServices.mBinder
        myMusicService = binders.currentService()
        Toast.makeText(this,"Connected$myMusicService", Toast.LENGTH_SHORT).show()
        binding.seekbar.max = (myMusicService!!.getDuration() / 1000)
        initialiseSeekBar()
        metaData(uri)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        myMusicService = null
    }

    fun showNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Log.d("ELSE1", "showNotification: If running")
            val prevIntent = Intent(this, NotificationReceiver::class.java)
                .setAction(ApplicationClass.ACTION_PREVIOUS)
            val prevPending = PendingIntent
                .getBroadcast(this,0,prevIntent, PendingIntent.FLAG_IMMUTABLE)

            val pauseIntent = Intent(this, NotificationReceiver::class.java)
                .setAction(ApplicationClass.ACTION_PLAY)
            val pausePending = PendingIntent
                .getBroadcast(this,1,pauseIntent, PendingIntent.FLAG_IMMUTABLE)

            val nextIntent = Intent(this, NotificationReceiver::class.java)
                .setAction(ApplicationClass.ACTION_NEXT)
            val nextPending = PendingIntent
                .getBroadcast(this,2,nextIntent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID_2)
                .setSmallIcon(R.drawable.big_musical_note)
                .setContentTitle(audioList[position].getaName())
                .setContentText(audioList[position].getaArtist())
                .addAction(R.drawable.ic_baseline_fast_rewind_24,"Previous",prevPending)
                .addAction(R.drawable.ic_baseline_pause_24,"Pause",pausePending)
                .addAction(R.drawable.ic_baseline_fast_forward_24,"Next",nextPending)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build()
            Log.d("ELSE1", "showNotification: $position")
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0,notification)
        }
    }


}