package com.toralapps.viowh.livecall.protodatastore

import androidx.datastore.core.Serializer
import com.toralapps.viowh.livecall.videolistmodel.VideoList
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class VIdeolistSerializer @Inject constructor():Serializer<VideoList> {
    override val defaultValue: VideoList
        get() = VideoList()

    override suspend fun readFrom(input: InputStream): VideoList {
      return try {
            Json.decodeFromString(
                deserializer = VideoList.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch (e:Exception){
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: VideoList, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = VideoList.serializer(),
                value  = t
            ).encodeToByteArray()
        )
    }
}