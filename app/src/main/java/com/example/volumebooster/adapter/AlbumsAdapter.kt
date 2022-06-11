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
import com.example.volumebooster.AudioModel
import com.example.volumebooster.MusicActivity
import com.example.volumebooster.databinding.ActivityAlbumsRecyclerviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumsAdapter(private val names: MutableList<AudioModel>, val context: Context):
    RecyclerView.Adapter<AlbumsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ActivityAlbumsRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name:String = names[position].aName.toString()
        val duration:String = names[position].aDuration.toString()
        val path:String = names[position].aPath.toString()
        holder.binding.name.text = names[position].aAlbum

        val mr = MediaMetadataRetriever()
        mr.setDataSource(path)
        val byte1 = mr.embeddedPicture
        mr.release()
        val art: Bitmap?
        var scaled: Bitmap? = null
        if (byte1 != null) {
            art = BitmapFactory.decodeByteArray(byte1, 0, byte1.size)
            val h = 100 // height in pixels
            val w = 100 // width in pixels

            scaled = Bitmap.createScaledBitmap(art, h, w, true)
            Log.d("Thumbnail1", "Thumbnail $art")

            //holder.binding.thumbnail.setImageBitmap(scaled)
        }
        CoroutineScope(Dispatchers.Default).launch() {
            Glide.with(context)
                .load(scaled)
                .also {
                    // When Glide is done preparing the resource,
                    // use Main thread to load into ImageView.
                    withContext(Dispatchers.Main) {
                        it.into(holder.binding.thumbnail)
                    }
                }
        }

        holder.binding.linearLayout.setOnClickListener {
            val intent = Intent(context.applicationContext, MusicActivity::class.java)
            intent.putExtra("position",position)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return names.size
    }

    class MyViewHolder(val binding: ActivityAlbumsRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)
}