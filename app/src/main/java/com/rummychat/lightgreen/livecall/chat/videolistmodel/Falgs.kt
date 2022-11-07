package com.rummychat.lightgreen.livecall.chat.videolistmodel

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Falgs(
    @DrawableRes val flag:Int,
    val category:String
) : Parcelable
