package com.example.volumebooster.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.volumebooster.ArtistsMusicActivity
import com.example.volumebooster.AudioModel
import com.example.volumebooster.MusicActivity
import com.example.volumebooster.databinding.ActivityArtistsRecyclerviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistsAdapter(private val names: MutableList<AudioModel>, val context: Context):
    RecyclerView.Adapter<ArtistsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ActivityArtistsRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val path:String = names[position].aPath.toString()
        holder.binding.name.text = names[position].aArtist

        val mr = MediaMetadataRetriever()
        mr.setDataSource(path)
        val byte1 = mr.embeddedPicture
        if (byte1 != null) {
            Glide.with(context)
                .asBitmap()
                .load(byte1)
                .into(holder.binding.thumbnail)
        }
        holder.binding.linearLayout.setOnClickListener {
            val intent = Intent(context.applicationContext, ArtistsMusicActivity::class.java)
            intent.putExtra("position",position)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return names.size
    }

    class MyViewHolder(val binding: ActivityArtistsRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)
}