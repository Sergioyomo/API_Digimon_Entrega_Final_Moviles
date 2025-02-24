package com.sgomez.navegaciondetalle.data.repositories.model

import com.sgomez.navegaciondetalle.model.MediaItem

data class Result(
    val name: String,
    val img: String,
    val level: String
)

fun Result.toMediaItem() = MediaItem(
    name,
    img,
    level
)