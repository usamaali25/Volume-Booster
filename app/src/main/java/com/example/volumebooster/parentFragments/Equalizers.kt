package com.example.volumebooster.parentFragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.Equalizer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bullhead.equalizer.EqualizerFragment
import com.example.volumebooster.R
import com.example.volumebooster.databinding.FragmentEqualizerBinding


class Equalizers : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var adapter: ArrayAdapter<CharSequence>
    private var _binding: FragmentEqualizerBinding? = null
    private val binding get() = _binding!!
    var mediaPlayer = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEqualizerBinding.inflate(inflater,container,false)
        val view = binding.root
        addView(view)
        return view
    }

    @SuppressLint("MissingPermission")
    private fun addView(view: View?) {
        /*try{
            val sessionId = mediaPlayer.audioSessionId
            mediaPlayer.isLooping = true
            val equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#6aefae"))
                .setAudioSessionId(sessionId)
                .build()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment)
                .commit()

        }
        catch (e:Exception)
        {
            Log.d("MSG1", "addView: ${e.message}")
            Toast.makeText(requireContext(),"You have no Equalizer",Toast.LENGTH_SHORT).show()
        }*/
        /*val equalizer = Equalizer(0,mediaPlayer.audioSessionId)
        val bands = equalizer.numberOfBands
        val minEQLevel = equalizer.bandLevelRange[0]
        val maxEQLevel = equalizer.bandLevelRange[1]*/
        binding.equalizerSwitch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked)
            {
                true ->{binding.linearLayout.isVisible = true}
                false ->{binding.linearLayout.isVisible = false}
            }
        }


        adapter = view?.let { ArrayAdapter.createFromResource(it.context,R.array.custom, android.R.layout.simple_spinner_item) }!!
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.equalizerPresetSpinner.adapter = adapter
        binding.equalizerPresetSpinner.onItemSelectedListener = this

        val vibration = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        binding.controllerBass.setOnProgressChangedListener {
            if (Build.VERSION.SDK_INT >= 26) {
                vibration.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibration.vibrate(30)
            }
        }

        binding.virBooster.setOnProgressChangedListener {
            if (Build.VERSION.SDK_INT >= 26) {
                vibration.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibration.vibrate(30)
            }
        }


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text = parent?.getItemAtPosition(position).toString()
        when (position) {
            1 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 30
                binding.seekBar2.progress = 60
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 70
                binding.seekBar5.progress = 60
            }
            2 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 70
                binding.seekBar2.progress = 60
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 60
                binding.seekBar5.progress = 60
            }
            3 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 70
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 60
                binding.seekBar4.progress = 65
                binding.seekBar5.progress = 60
            }
            4 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 40
                binding.seekBar2.progress = 40
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 40
                binding.seekBar5.progress = 40
            }
            5 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 50
                binding.seekBar2.progress = 40
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 40
            }
            6 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 60
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 90
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 40
            }
            7 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 60
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 45
                binding.seekBar5.progress = 50
            }
            8 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 55
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 30
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 60
            }
            9 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 40
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 60
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 40
            }
            10 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 60
                binding.seekBar2.progress = 55
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 60
            }
            0 -> {
                Log.d("POS1", "onItemSelected: $position")
                binding.seekBar1.progress = 30
                binding.seekBar2.progress = 40
                binding.seekBar3.progress = 50
                binding.seekBar4.progress = 60
                binding.seekBar5.progress = 70
            }
        }

        Log.d("TEXT1", "onItemSelected: $text")
        //Toast.makeText(parent?.context, text, Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    /*val sessionId = player!!.audioSessionId
    com.bullhead.equalizer.Settings.isEditing = false
    var equalizerFragment = EqualizerFragment.newBuilder()
        .setAccentColor(R.color.purple_200)
        .setAudioSessionId(sessionId)
        .build()
    parentFragmentManager.beginTransaction()
        .replace(R.id.eqFrame,equalizerFragment)
        .commit()
    try {

        val i = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, player!!.audioSessionId)
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context!!.packageName)
        i.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        startActivityForResult(i, 13)
    }catch (e:Exception) {Toast.makeText(activity, "Equalizer Feature not supported", Toast.LENGTH_SHORT).show()}*/


}