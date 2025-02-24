package com.sgomez.navegaciondetalle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sgomez.navegaciondetalle.ui.navegacion.Navegacion
import com.sgomez.navegaciondetalle.ui.screen.ListaScreen.ListaScreen
import com.sgomez.navegaciondetalle.ui.theme.NavegacionDetalleTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.ktx.analytics
import com.sgomez.navegaciondetalle.data.AuthManager

class MainActivity : ComponentActivity() {
    val auth = AuthManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Firebase.analytics
        setContent {
            NavegacionDetalleTheme {
                Navegacion(auth)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }
}

