package com.sgomez.navegaciondetalle.data.model

data class Favorito(
    var id: String? = null,
    val userId: String?,
    val favorito: Boolean?,
    val nombre: String?
)
