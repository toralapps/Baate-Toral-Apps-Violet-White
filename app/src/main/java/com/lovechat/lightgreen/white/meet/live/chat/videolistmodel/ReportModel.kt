package com.lovechat.lightgreen.white.meet.live.chat.videolistmodel

import androidx.annotation.Keep


@Keep
data class ReportModel(
    val Data: Any,
    val HttpStatus: Int,
    val Message: String,
    val Status: Boolean
)