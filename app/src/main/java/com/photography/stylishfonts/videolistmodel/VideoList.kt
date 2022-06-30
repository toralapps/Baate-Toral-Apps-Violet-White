package com.photography.stylishfonts.videolistmodel

import androidx.annotation.Keep

@Keep
data class VideoList(
    val Data: List<Data>,
    val HttpStatus: Int,
    val Message: String,
    val Status: Boolean
)