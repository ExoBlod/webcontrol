package com.webcontrol.android.util

import android.util.Base64
import com.google.gson.*
import java.lang.reflect.Type

class ByteArrayToBase64TypeAdapter : JsonSerializer<ByteArray?>, JsonDeserializer<ByteArray> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement
                             , typeOfT: Type, context: JsonDeserializationContext): ByteArray {
        return Base64.decode(json.asString, Base64.NO_WRAP)
    }

    override fun serialize(src: ByteArray?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP))
    }
}