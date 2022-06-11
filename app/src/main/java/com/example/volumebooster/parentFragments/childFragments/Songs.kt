package com.example.volumebooster.parentFragments.childFragments

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.volumebooster.AudioModel
import com.example.volumebooster.GlobalStuff
import com.example.volumebooster.GlobalStuff.songsList
import com.example.volumebooster.GlobalStuff.tempAudioList
import com.example.volumebooster.R
import com.example.volumebooster.adapter.SongsAdapter


class Songs : Fragment() {


    var count = 0
    private lateinit var view1:View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:SongsAdapter
    private lateinit var nestedSV:NestedScrollView
    var isLoading = false
    private lateinit var loadingPB:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(R.layout.fragment_songs, container, false)
        addView(view1)
        return view1
    }

    private fun addView(view1: View?) {
        context?.let { getAllAudioFromDevice(it) }
        //context?.let { getMp3(it) }
        //loadFiles()
        recyclerView = view1?.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view1.context)

        adapter = SongsAdapter(tempAudioList, view1.context)
        recyclerView.adapter = adapter
        initScrollListener()


        /*nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.
                count++
                // on below line we are making our progress bar visible.

                if (count < 20) {
                    // on below line we are again calling
                    // a method to load data in our array list.
                    context?.let { getAllAudioFromDevice(it) }
                }
            }
        })*/
    }

    @SuppressLint("Range")
    private fun getAllAudioFromDevice(context: Context): List<AudioModel> {

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.AudioColumns._ID
        )

        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " ASC"
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
                val id = c.getLong(c.getColumnIndexOrThrow(projection[5]))
                val audioModel = AudioModel()
                val path: String = c.getString(0)
                val album: String = c.getString(1)
                val artist: String = c.getString(2)
                val name: String = c.getString(3)
                val duration: Int = c.getInt(4)
                val contentUri = ContentUris.withAppendedId(uri, id)

                with(audioModel) {
                    setaName(name)
                    setaAlbum(album)
                    setaArtist(artist)
                    setaPath(path)
                    setaDuration(duration)
                    setaUri(contentUri)
                }
                if (path.endsWith(".mp3")) {
                    tempAudioList.add(audioModel)
                }


                Log.d("Name1", "Names: $name")
                /*Log.d("Name1", "Albums: $album")
                Log.d("Name1", "Artists: $artist")
                Log.e("Name1 :$name", " Album :$album")
                Log.e("Path1 :$path", " Artist :$artist")*/
               /* if (path.endsWith(".mp3")) {
                    tempAudioList.add(audioModel)
                }*/

            }
            c.close()
        }
        return tempAudioList
    }

    private fun initScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == tempAudioList.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore() {
        adapter.notifyItemInserted(tempAudioList.size - 1)
        val handler = Handler()
        handler.postDelayed({
            tempAudioList.removeAt(tempAudioList.size - 1)
            val scrollPosition: Int = tempAudioList.size
            adapter.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 10
            while (currentSize - 1 < nextLimit) {
                context?.let { getAllAudioFromDevice(it) }
                currentSize++
            }
            adapter.notifyDataSetChanged()
            isLoading = false
        }, 2000)
    }

    /*private fun getData() {
        // creating a new variable for our request queue
        val queue = Volley.newRequestQueue(context)
        // in this case the data we are getting is in the form
        // of array so we are making a json array request.
        // below is the line where we are making an json array
        // request and then extracting data from each json object.
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->

                for (i in 0 until response.length()) {
                    // creating a new json object and
                    // getting each object from our json array.
                    try {
                        // we are getting each json object.
                        val responseObj = response.getJSONObject(i)

                        // now we get our response from API in json object format.
                        // in below line we are extracting a string with
                        // its key value from our json object.
                        // similarly we are extracting all the strings from our json object.
                        val courseName = responseObj.getString("courseName")
                        val courseTracks = responseObj.getString("courseTracks")
                        val courseMode = responseObj.getString("courseMode")
                        val courseImageURL = responseObj.getString("courseimg")
                        tempAudioList.add(
                            (
                                courseName,
                                courseMode,
                                courseTracks,
                                courseImageURL
                            )
                        )

                        // on below line we are adding our array list to our adapter class.
                        adapter = SongsAdapter(tempAudioList, view1.context)

                        // on below line we are setting
                        // adapter to our recycler view.
                        recyclerView.adapter = adapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        ) { Toast.makeText(context, "Fail to get the data..", Toast.LENGTH_SHORT).show() }
        queue.add(jsonArrayRequest)
    }*/

    companion object{
        var tempAudioList: MutableList<AudioModel> = ArrayList()
    }
}