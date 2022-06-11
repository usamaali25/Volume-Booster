package com.example.volumebooster

import android.net.Uri
import java.net.URI


class AudioModel{
    var aPath: String? = null
    var aName: String? = null
    var aAlbum: String? = null
    var aArtist: String? = null
    var aDuration: Int? = null
    var uri:Uri? = null


    fun getaPath(): String? {
        return aPath
    }


    fun setaPath(aPath: String?) {
        this.aPath = aPath
    }

    fun getaName(): String? {
        return aName
    }

    fun setaName(aName: String?) {
        this.aName = aName
    }

    fun getaAlbum(): String? {
        return aAlbum
    }

    fun setaAlbum(aAlbum: String?) {
        this.aAlbum = aAlbum
    }

    fun getaArtist(): String? {
        return aArtist
    }

    fun setaArtist(aArtist: String?) {
        this.aArtist = aArtist
    }

    fun getaDuration():Int? {
        return aDuration
    }

    fun setaDuration(aDuration: Int)
    {
        this.aDuration = aDuration
    }

    fun getaUri(): Uri? {
        return uri
    }


    fun setaUri(uri: Uri) {
        this.uri = uri
    }

}