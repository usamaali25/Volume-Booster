package com.example.volumebooster.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.volumebooster.AudioModel
import com.example.volumebooster.MusicActivity
import com.example.volumebooster.databinding.ActivitySongsRecyclerviewBinding
import kotlinx.coroutines.*


class SongsAdapter(private val names: MutableList<AudioModel>, val context: Context) :
    RecyclerView.Adapter<SongsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ActivitySongsRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name:String = names[position].aName.toString()
        val duration:String = names[position].aDuration.toString()
        val path:String = names[position].aPath.toString()
        holder.binding.name.text = names[position].aName
        Log.d("NAME2", "onBindViewHolder: ${names[position].aDuration.toString()}")
        Log.d("Thumbnail1", "Thumbnail ${names[position].getaUri()} ${names[position].aName.toString()}")
        val image = getAlbumArt(names[position].getaPath()!!)
        /*if (image != null)
        {
            Glide.with(context)
                .load(image)
                .into(holder.binding.thumbnail)
        }*/
        val mr = MediaMetadataRetriever()
        mr.setDataSource(path)
        val byte1 = mr.embeddedPicture
        /*mr.release()
        val art: Bitmap?
        var scaled: Bitmap? = null*/
        if (byte1 != null) {
            /*art = BitmapFactory.decodeByteArray(byte1, 0, byte1.size)
            val h = 100 // height in pixels
            val w = 100 // width in pixels

            scaled = Bitmap.createScaledBitmap(art, h, w, true)
            Log.d("Thumbnail2", "Thumbnail $art")*/
            Glide.with(context)
                .asBitmap()
                .load(byte1)
                .into(holder.binding.thumbnail)
            //holder.binding.thumbnail.setImageBitmap(scaled)
        }

       /* CoroutineScope(Dispatchers.Default).launch() {
            Glide.with(context)
                .load(scaled)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .also {
                    // When Glide is done preparing the resource,
                    // use Main thread to load into ImageView.
                    withContext(Dispatchers.Main) {
                        it.into(holder.binding.thumbnail)
                    }
                }
        }*/

        /*Picasso.get()
            .load(File(names[position].getaUri().toString()))
            .into(holder.binding.thumbnail)*/

        holder.binding.linearLayout.setOnClickListener {
            val intent = Intent(context.applicationContext, MusicActivity::class.java)
            /*val extras = Bundle()
            extras.putString("duration",duration)
            extras.putString("name",name)
            extras.putString("path",path)
            intent.putExtras(extras)*/
            intent.putExtra("position",position)

            context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
        return names.size
    }

    class MyViewHolder(val binding: ActivitySongsRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

    fun getAlbumArt(uri: String): ByteArray?
    {
        val mr = MediaMetadataRetriever()
        mr.setDataSource(uri)
        val byte1 = mr.embeddedPicture
        mr.release()
        return byte1
    }

}
/*val gson = Gson()
            val jsonMusic: String = gson.toJson(names)
            val intent = Intent(context.applicationContext, MusicActivity::class.java)
            intent.putExtra("arrayList",jsonMusic)*/