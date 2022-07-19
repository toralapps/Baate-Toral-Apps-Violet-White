package com.lovechat.red.pink.girl.dating.call.videolistmodel

import androidx.annotation.Keep

@Keep
data class VideoList(
    val Data: List<Data>,
    val Settings:VideoCallType,
    val HttpStatus: Int,
    val Message: String,
    val Status: Boolean
)