package com.rummychat.lightgreen.livecall.chat.videolistmodel

import androidx.annotation.Keep


@Keep
data class ReportModel(
    val Data: Any,
    val HttpStatus: Int,
    val Message: String,
    val Status: Boolean
)