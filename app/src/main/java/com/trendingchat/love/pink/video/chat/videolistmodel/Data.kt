package com.trendingchat.love.pink.video.chat.videolistmodel

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val Comments: Any,
    val FirstName: String,
    val LastName: String,
    val Likes: Any,
    val Location: Any,
    val Loves: Any,
    val Smiles: Any,
    val SrNo: Any,
    val ThumbnailUrl: String,
    val VideoId: Int,
    val VideoName: String,
    val VideoUrl: String,
    val Views: Any
)