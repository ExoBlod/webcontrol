package com.webcontrol.angloamerican.ui.checklistpreuso.data.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeQuestionResponse
import java.lang.reflect.Type

class TypeQuestionResponseAdapter : JsonDeserializer<TypeQuestionResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TypeQuestionResponse {
        val tipo = json?.asString
        return when (tipo) {
            "NORMAL" -> TypeQuestionResponse.responseWithNA
            "SN" -> TypeQuestionResponse.responseYesOrNo
            else -> TypeQuestionResponse.responseWithNA
        }
    }
}