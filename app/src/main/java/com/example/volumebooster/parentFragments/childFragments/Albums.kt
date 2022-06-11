package com.example.volumebooster.parentFragments.childFragments

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.volumebooster.AudioModel
import com.example.volumebooster.R
import com.example.volumebooster.adapter.AlbumsAdapter
import com.example.volumebooster.adapter.ArtistsAdapter


class Albums : Fragment() {

    private lateinit var view1:View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlbumsAdapter
    private val tempAudioList: MutableList<AudioModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view1 = inflater.inflate(R.layout.fragment_albums, container, false)
        addView(view1)
        return view1
    }

    private fun addView(view1: View?) {
        context?.let { getAllAudioFromDevice(it) }
        recyclerView = view1?.findViewById(R.id.albums_recycler_view) as RecyclerView
        GridLayoutManager(view1.context, 3, RecyclerView.VERTICAL, false)
            .apply { recyclerView.layoutManager = this }
        adapter = AlbumsAdapter(tempAudioList,view1.context)
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }

    private fun getAllAudioFromDevice(context: Context): List<AudioModel> {

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val c: Cursor? = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )

        if (c != null)
        {
            c.moveToFirst()
            while ( c.moveToNext()) {
                Log.d("Cursor1", "While is running")
                val audioModel = AudioModel()
                val path: String = c.getString(0)
                val album: String = c.getString(1)
                val artist: String = c.getString(2)
                val name: String = c.getString(3)
                val duration: Int = c.getInt(4)

                with(audioModel) {
                    setaName(name)
                    setaAlbum(album)
                    setaArtist(artist)
                    setaPath(path)
                    setaDuration(duration)
                }

                Log.d("Name1", "Names: $name")
                /*Log.d("Name1", "Albums: $album")
                Log.d("Name1", "Artists: $artist")
                Log.e("Name1 :$name", " Album :$album")
                Log.e("Path1 :$path", " Artist :$artist")*/
                if (path.endsWith(".mp3")) {
                    tempAudioList.add(audioModel)
                }
            }
            c.close()
        }
        return tempAudioList
    }
}