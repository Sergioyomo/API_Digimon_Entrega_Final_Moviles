package com.sgomez.navegaciondetalle.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgomez.navegaciondetalle.data.AuthManager
import com.sgomez.navegaciondetalle.data.AuthRes
import com.sgomez.navegaciondetalle.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun SignUpScreen(auth: AuthManager, navigateToHome: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear cuenta",
                style = TextStyle(fontSize = 40.sp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    scope.launch() { signUp(navigateToHome, auth, email, password, context) }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Registrarse")
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    textDecoration = TextDecoration.Underline,
                    color = Purple40
                ),
                modifier = Modifier.clickable { navigateToHome() }
            )
        }

    }
}

suspend fun signUp(navigateToHome: () -> Unit, auth: AuthManager, email: String, password: String, context: Context) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        when (
            val result = withContext(Dispatchers.IO){
                auth.createUserWithEmailAndPassword(email, password)
            }
             ) {
            is AuthRes.Success -> {
                Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }

            is AuthRes.Error -> {
                Toast.makeText(context, "Error al crear usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
    else {
        Toast.makeText(context, "Llene todos los campos", Toast.LENGTH_SHORT).show()
    }

}

