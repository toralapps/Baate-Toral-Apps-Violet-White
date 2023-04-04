package com.toralapps.viowh.livecall.videolistmodel

import androidx.annotation.Keep

@Keep
@kotlinx.serialization.Serializable
data class Data(
    val Comments: String?,
    val FirstName: String?,
    val LastName: String?,
    val Likes: String?,
    val Location: String?,
    val Loves: String?,
    val Smiles: String?,
    val SrNo: String?,
    val ThumbnailUrl: String,
    val VideoId: Int,
    val VideoName: String,
    val VideoUrl: String,
    val Views: String?
)