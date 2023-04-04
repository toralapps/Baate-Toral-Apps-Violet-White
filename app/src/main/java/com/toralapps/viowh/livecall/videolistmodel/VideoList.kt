package com.toralapps.viowh.livecall.videolistmodel

import androidx.annotation.Keep

@Keep
@kotlinx.serialization.Serializable
data class VideoList(
    val Data: List<Data> = emptyList(),
    val Settings: VideoCallType = VideoCallType("prank"),
    val HttpStatus: Int = 404,
    val Message: String = "",
    val Status: Boolean = true
)