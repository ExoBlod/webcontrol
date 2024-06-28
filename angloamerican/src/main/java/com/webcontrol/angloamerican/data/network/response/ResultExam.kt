package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

data class ResultExam(
    var Aprobo: String = "",
    var Nota: Int = 0,
    var ListVideo: List<ListVideo> = listOf()
)
