package com.app.ads.protoserializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.app.ads.CachVideos
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class CachVideoSerializer @Inject constructor ():Serializer<CachVideos> {
    override val defaultValue: CachVideos
        get() = CachVideos.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CachVideos {
        try {
          return  CachVideos.parseFrom(input)
        }catch (e: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: CachVideos, output: OutputStream) {
        t.writeTo(output)
    }

}