package com.sgomez.navegaciondetalle.ui.navegacion

import kotlinx.serialization.Serializable

@Serializable
object Lista

@Serializable
data class Detail(val name: String)

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object ForgotPassword
