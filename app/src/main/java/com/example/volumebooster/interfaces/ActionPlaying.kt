package com.example.volumebooster.interfaces

import android.net.Uri

interface ActionPlaying {
    fun backBtnClicked()
    fun nextBtnClicked()
    fun startPlaying(uri: Uri)
    fun pausePlaying()
}