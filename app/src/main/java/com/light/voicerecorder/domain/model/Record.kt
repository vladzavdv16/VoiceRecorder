package com.light.voicerecorder.domain.model


data class Record(
    val id: Long,
    var name: String,
    var filePath: String,
    var length: Long,
    var time: Long,
)