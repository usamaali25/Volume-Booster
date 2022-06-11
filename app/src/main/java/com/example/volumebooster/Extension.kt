package com.example.volumebooster

object GlobalStuff{
    var tempAudioList: MutableList<AudioModel>? = null

    fun songsList(audioModel: AudioModel) {
        tempAudioList!!.add(audioModel)
    }
}
