package com.toopchart.redpink.video.chat.call.videolistmodel

import androidx.annotation.Keep

@Keep
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