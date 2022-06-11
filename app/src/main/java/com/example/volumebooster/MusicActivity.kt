package com.example.volumebooster


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.volumebooster.ApplicationClass.Companion.ACTION_NEXT
import com.example.volumebooster.ApplicationClass.Companion.ACTION_PLAY
import com.example.volumebooster.ApplicationClass.Companion.ACTION_PREVIOUS
import com.example.volumebooster.ApplicationClass.Companion.CHANNEL_ID_2
import com.example.volumebooster.databinding.ActivityMusicBinding
import com.example.volumebooster.interfaces.ActionPlaying
import com.example.volumebooster.notification.NotificationReceiver
import com.example.volumebooster.parentFragments.childFragments.Songs
import com.example.volumebooster.services.MusicServices
import java.io.IOException
import kotlin.math.log


class MusicActivity : AppCompatActivity(),SeekBar.OnSeekBarChangeListener,ActionPlaying,ServiceConnection {

    companion object{
        var musicServices:MusicServices? = null
    }

    private lateinit var binding: ActivityMusicBinding
    private lateinit var audioList: MutableList<AudioModel>
    //private var mPlayer: MediaPlayer? = null
    private lateinit var paths:String
    lateinit var uri: Uri
    private lateinit var audioManager: AudioManager
    var position = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        getIntentMethod()
        binding.songName.text = audioList[position].getaName()
        binding.back.setOnClickListener {
            onBackPressed()
        }
        /*val intent = Intent(this,MusicServices::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)*/
                            //GetExtra to get the values from previous class
        /*val carListAsString = intent.getStringExtra("list_as_string")

        val gson = Gson()
        val type: Type = object : TypeToken<List<Cars?>?>() {}.type
        val carsList: List<Cars> = gson.fromJson<List<Cars>>(carListAsString, type)*/

        /*val intent = intent

        //audioList = Songs.tempAudioList
        val extras = intent.extras
        val duration = extras?.getString("duration")
        val names = extras?.getString("name")
        val path = extras?.getString("path")
        Log.d("PATH1", "onCreate: $path")
        paths = path.toString() //Assignment to global variable
        startPlaying()
        Log.d("DUR1", "onCreate: $duration")
        val dur:Int = duration.let { Integer.parseInt(it.toString()) } //Integer Parsing
                            //Setting Text to the view
        binding.songName.text = names
        binding.totalTime.text = DateUtils.formatElapsedTime((dur/1000).toLong())
        val mr = MediaMetadataRetriever()
        mr.setDataSource(path)
        val byte1 = mr.embeddedPicture
        mr.release()
        val art: Bitmap?
        if (byte1 != null) {
            art = BitmapFactory.decodeByteArray(byte1, 0, byte1.size)
            val h = 100 // height in pixels
            val w = 100 // width in pixels
            val scaled = Bitmap.createScaledBitmap(art, h, w, true)
            Log.d("Thumbnail1", "Thumbnail $art")
            binding.thumbnail.setImageBitmap(scaled)
        }

                            //OnClick Listener

                            //SeekBar Functionality
        binding.seekbar.progress = 0
        binding.seekbar.max = Integer.parseInt(duration.toString())
        //val seekbar = SeekBar(this)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager*/
        var b = false //Boolean value
        binding.start.setOnClickListener {
            if(b == false)
            {
                startPlaying(uri)
                //showNotification()
                binding.start.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                b= true
            }
            else
            {
                pausePlaying()
                //showNotification()
                binding.start.setImageResource(R.drawable.ic_baseline_pause_24)
                b=false
                // enter pause code here
            }

        }

        binding.seekbar.setOnSeekBarChangeListener(this)

    }


    override fun onResume() {
        super.onResume()
        val intent = Intent(this,MusicServices::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }else {
            startService(intent)
        }
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
        if (musicServices!!.isPlaying())
        {
            musicServices!!.stop()
            musicServices!!.release()
            Log.d("POS1", "backBtnClicked: $position")
            if (position == 0)
            {
                position = audioList.size - 1
            }
            else
            {
                position = ((position - 1) % audioList.size)
            }

            uri = Uri.parse(audioList[position].getaPath())
            //mPlayer = MediaPlayer.create(this,uri)
            musicServices!!.createMediaPlayer(position)
            metaData(uri)
            binding.songName.text = audioList[position].aName
            var b = false //Boolean value
            startPlaying(uri)
            binding.start.setOnClickListener {
                if(b == false)
                {
                    startPlaying(uri)
                    //showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    b= true
                }
                else
                {
                    pausePlaying()
                    //showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_pause_24)
                    b=false
                    // enter pause code here
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
        if (musicServices!!.isPlaying())
        {
            musicServices!!.stop()
            musicServices!!.release()
            position = ((position + 1) % audioList.size)
            uri = Uri.parse(audioList[position].getaPath())
            musicServices!!.createMediaPlayer(position)
            metaData(uri)
            binding.songName.text = audioList[position].aName
            var b = false //Boolean value
            startPlaying(uri)
            binding.start.setOnClickListener {
                if(b == false)
                {
                    startPlaying(uri)
                    //showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    b= true
                }
                else
                {

                    pausePlaying()
                    //showNotification()
                    binding.start.setImageResource(R.drawable.ic_baseline_pause_24)
                    b=false
                    // enter pause code here
                }

            }
        }
    }

    /*private fun metaData(uri: Uri?) {
        val mr = MediaMetadataRetriever()
        mr.setDataSource(uri.toString())
        val durationTotal = Integer.parseInt(audioList[position].getaDuration().toString())
        binding.totalTime.text = DateUtils.formatElapsedTime((dur/1000).toLong())
        val byte1 = mr.embeddedPicture
        mr.release()
        val art :Bitmap?
        if (byte1 != null)
        {
            art = BitmapFactory.decodeByteArray(byte1, 0, byte1.size)
            val h = 100 // height in pixels
            val w = 100 // width in pixels
            val scaled = Bitmap.createScaledBitmap(art, h, w, true)
            Log.d("Thumbnail1", "Thumbnail $art")
            binding.thumbnail.setImageBitmap(scaled)
        }

    }*/




    fun getIntentMethod()
    {
        position = intent.getIntExtra("position",-1)
        Log.d("POS2", "getIntentMethod: $position")
        audioList = Songs.tempAudioList
        if (audioList.isNotEmpty())
        {
            uri = Uri.parse(audioList[position].getaPath())
        }
        /*if (musicServices !=null)
        {
            musicServices!!.stop()
            musicServices!!.release()
        }
        else
        {
            musicServices!!.createMediaPlayer(position)
            musicServices!!.start()
        }*/
        val intent = Intent(this, MusicServices::class.java)
        intent.putExtra("position",position)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }
        else
            startService(intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LIFE1", "onDestroy: ")
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
            musicServices?.seekTo(p1)
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
        if (musicServices != null && musicServices!!.isPlaying()) {
            initialiseSeekBar()
            musicServices!!.pause()

        } else if (musicServices != null) {
            musicServices!!.start()
        } else {
            musicServices = MusicServices()
            try {
                musicServices!!.setDataSource(uri)
                musicServices!!.prepare()
                musicServices!!.start()
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
        binding.seekbar.max = musicServices!!.getDuration()
        val handler = Handler()
        handler.postDelayed(object : Runnable{
            override fun run() {
                try {
                    binding.seekbar.progress = musicServices!!.currentPosition()
                    val currentDuration = musicServices!!.currentPosition()
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
        if (musicServices!!.isPlaying()) {
            musicServices!!.pause()
        } else {
            musicServices!!.start()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicServices.MyBinder
        musicServices = binder.currentService()
        musicServices!!.setCallBack(this)
        Toast.makeText(this,"Connected$musicServices",Toast.LENGTH_SHORT).show()
        binding.seekbar.max = (musicServices!!.getDuration() / 1000)
        binding.songName.text = audioList[position].getaName()

        initialiseSeekBar()
        metaData(uri)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicServices = null
    }


    /*fun showNotification()
    {
       *//*val intent = Intent(this,MusicActivity::class.java)
       val contentIntent = PendingIntent.getActivity(this,0,intent,0)*//*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val prevIntent = Intent(this, NotificationReceiver::class.java)
                .setAction(ACTION_PREVIOUS)
            val prevPending = PendingIntent
                .getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE)

            val pauseIntent = Intent(this, NotificationReceiver::class.java)
                .setAction(ACTION_PLAY)
            val pausePending = PendingIntent
                .getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)

            val nextIntent = Intent(this, NotificationReceiver::class.java)
                .setAction(ACTION_NEXT)
            val nextPending = PendingIntent
                .getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.big_musical_note)
                .setContentTitle(audioList[position].getaName())
                .setContentText(audioList[position].getaArtist())
                .addAction(R.drawable.ic_baseline_fast_rewind_24, "Previous", prevPending)
                .addAction(R.drawable.ic_baseline_pause_24, "Pause", pausePending)
                .addAction(R.drawable.ic_baseline_fast_forward_24, "Next", nextPending)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build()

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notification)
        }


    }
*/}