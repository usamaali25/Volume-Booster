package com.example.volumebooster.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.volumebooster.ApplicationClass.Companion.ACTION_NEXT
import com.example.volumebooster.ApplicationClass.Companion.ACTION_PLAY
import com.example.volumebooster.ApplicationClass.Companion.ACTION_PREVIOUS
import com.example.volumebooster.ApplicationClass.Companion.CHANNEL_ID_2
import com.example.volumebooster.AudioModel
import com.example.volumebooster.R
import com.example.volumebooster.interfaces.ActionPlaying
import com.example.volumebooster.notification.NotificationReceiver
import com.example.volumebooster.parentFragments.childFragments.Songs
import android.media.audiofx.Equalizer
import com.example.volumebooster.parentFragments.childFragments.Artists
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ArtistsMusicServices: Service() {

    companion object{
        var mPlayer:MediaPlayer? = null
    }
    private var myBinder = mBinder()

    lateinit var audioFiles:MutableList<AudioModel>
    lateinit var uri:Uri
    lateinit var actionPlaying:ActionPlaying
    var position:Int = -1
    lateinit var mediaSessionCompat: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        audioFiles = Artists.tempAudioList
        mediaSessionCompat = MediaSessionCompat(baseContext,"My Audio")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val pos = intent?.getIntExtra("position",-1)
        val actionName = intent?.getStringExtra("ActionName")
        if (pos!=-1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Log.d("ELSE1", "showNotification: If running")
                val prevIntent = Intent(this,NotificationReceiver::class.java)
                    .setAction(ACTION_PREVIOUS)
                val prevPending = PendingIntent
                    .getBroadcast(this,0,prevIntent,PendingIntent.FLAG_IMMUTABLE)

                val pauseIntent = Intent(this, NotificationReceiver::class.java)
                    .setAction(ACTION_PLAY)
                val pausePending = PendingIntent
                    .getBroadcast(this,1,pauseIntent,PendingIntent.FLAG_IMMUTABLE)

                val nextIntent = Intent(this,NotificationReceiver::class.java)
                    .setAction(ACTION_NEXT)
                val nextPending = PendingIntent
                    .getBroadcast(this,2,nextIntent,PendingIntent.FLAG_IMMUTABLE)

                val notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
                    .setSmallIcon(R.drawable.big_musical_note)
                    .setContentTitle(audioFiles[pos!!].getaName())
                    .setContentText(audioFiles[pos].getaArtist())
                    .addAction(R.drawable.ic_baseline_fast_rewind_24,"Previous",prevPending)
                    .addAction(R.drawable.ic_baseline_pause_24,"Pause",pausePending)
                    .addAction(R.drawable.ic_baseline_fast_forward_24,"Next",nextPending)
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true)
                    .build()
                Log.d("ELSE1", "showNotification: $pos")
                startForeground(1,notification)
            }
            else
            {
                Log.d("ELSE1", "showNotification: Else running")
            }
            Log.d("POS2", "onStartCommand: $pos")
            pos?.let { playMedia(it) }
        }

        if (actionName!=null)
        {
            when(actionName)
            {
                "playPause" ->{
                    var b = false //Boolean value

                    if (b == false) {
                        actionPlaying.startPlaying(uri)
                        b = true
                    } else {

                        actionPlaying.pausePlaying()
                        //showNotification()
                        b = false
                        // enter pause code here
                    }
                    Toast.makeText(this, "PlayPause", Toast.LENGTH_SHORT).show()

                }
                "next" -> {
                    actionPlaying.nextBtnClicked()
                    Toast.makeText(this,"Next",Toast.LENGTH_SHORT).show()
                }
                "previous" -> {
                    actionPlaying.backBtnClicked()
                    Toast.makeText(this,"Previous",Toast.LENGTH_SHORT).show()
                }
            }
        }
        return START_STICKY
    }


    private fun playMedia(startPosition: Int) {
        audioFiles = Songs.tempAudioList
        position = startPosition
        if (mPlayer!=null)
        {
            mPlayer!!.stop()
            mPlayer!!.release()
            if (audioFiles.isNotEmpty())
            {
                createMediaPlayer(position)
                mPlayer!!.start()
            }
        }
        else
        {
            createMediaPlayer(position)
            mPlayer!!.start()
        }
    }

    inner class mBinder:Binder()
    {
        fun currentService(): ArtistsMusicServices
        {
            return this@ArtistsMusicServices
        }
    }
    fun start()
    {
        mPlayer!!.start()
    }
    fun stop()
    {
        mPlayer!!.stop()
    }
    fun isPlaying():Boolean
    {
        return mPlayer!!.isPlaying
    }
    fun release()
    {
        mPlayer!!.release()
    }
    fun getDuration() :Int
    {
        return mPlayer!!.duration
    }
    fun seekTo(position:Int)
    {
        mPlayer!!.seekTo(position)
    }
    fun createMediaPlayer(position:Int)
    {
        uri = Uri.parse(audioFiles[position].getaPath())
        Log.d("POS2", "createMediaPlayer: $position")
        mPlayer = MediaPlayer.create(baseContext,uri)
    }
    fun pause()
    {
        mPlayer!!.pause()
    }
    fun currentPosition():Int
    {
        return mPlayer!!.currentPosition
    }
    fun prepare()
    {
        mPlayer!!.prepare()
    }
    fun setDataSource(uri: Uri)
    {
        mPlayer!!.setDataSource(uri.toString())
    }
    fun setCallBack(actionPlaying: ActionPlaying)
    {
        this.actionPlaying = actionPlaying
    }


}