package com.toopchart.maroon.blue.love.video.call.videolistmodel

import androidx.annotation.Keep


@Keep
data class ReportModel(
    val Data: Any,
    val HttpStatus: Int,
    val Message: String,
    val Status: Boolean
)