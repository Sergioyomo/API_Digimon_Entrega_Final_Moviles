package com.sgomez.navegaciondetalle.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sgomez.navegaciondetalle.data.AuthManager
import com.sgomez.navegaciondetalle.data.FirestoreManager
import com.sgomez.navegaciondetalle.ui.screen.DetailScreen.DetailScreen
import com.sgomez.navegaciondetalle.ui.screen.ListaScreen.ListaScreen
import com.sgomez.navegaciondetalle.ui.screen.ListaScreen.ListaViewModel
import com.sgomez.navegaciondetalle.ui.screen.LoginScreen
import com.sgomez.navegaciondetalle.ui.screen.SignUpScreen

@Composable
fun Navegacion(auth: AuthManager) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val firestore = FirestoreManager(auth, context)

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                auth,
                { navController.navigate(SignUp) },
                {
                    navController.navigate(Lista) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                { navController.navigate(ForgotPassword) }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                auth
            ) { navController.popBackStack() }
        }
        composable<Lista> {
            val viewModel = ListaViewModel()
            ListaScreen(viewModel,firestore,auth) { name ->
                navController.navigate(Detail(name))
            }
        }
        composable<Detail> { backStackEntry ->
            val detail = backStackEntry.toRoute<Detail>()
            val name = detail.name
            DetailScreen(name,firestore,auth,navController)
            
        }
    }
}
