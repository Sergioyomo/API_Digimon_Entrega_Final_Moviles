package com.sgomez.navegaciondetalle.data.model

data class Dislike(
    var id: String? = null,
    val userId: String?,
    val dislike: Boolean?,
    val nombre: String?
)
