package com.baatechat.blackwhite.swip.call.videolistmodel

import androidx.annotation.Keep


@Keep
data class ReportModel(
    val Data: Any,
    val HttpStatus: Int,
    val Message: String,
    val Status: Boolean
)