package com.app.ads.protoserializer

import androidx.datastore.core.DataStore
import com.app.ads.CachVideos
import javax.inject.Inject

class CacheVideosSource @Inject constructor(private val cachVideos: DataStore<CachVideos>) {


}