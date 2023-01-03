package com.app.ads.adsprotodatastore

import androidx.datastore.core.Serializer
import com.app.ads.domain.models.AdsRoot
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.serializer
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AdsSerializer @Inject constructor():Serializer<AdsRoot> {
    override val defaultValue: AdsRoot
        get() = AdsRoot()

    override suspend fun readFrom(input: InputStream): AdsRoot {
      return try {
            Json.decodeFromString(
                deserializer = AdsRoot.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch (e:Exception){
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AdsRoot, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AdsRoot.serializer(),
                value  = t
            ).encodeToByteArray()
        )
    }
}