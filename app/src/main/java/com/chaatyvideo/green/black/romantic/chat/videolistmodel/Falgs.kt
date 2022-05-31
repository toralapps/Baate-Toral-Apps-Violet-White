package com.chaatyvideo.green.black.romantic.chat.videolistmodel

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Falgs(
    @DrawableRes val flag:Int,
    val startAge:String,
    val endAge:String
) : Parcelable
