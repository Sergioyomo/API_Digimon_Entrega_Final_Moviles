package com.sgomez.navegaciondetalle.ui.screen.ListaScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.sgomez.navegaciondetalle.data.AuthManager
import com.sgomez.navegaciondetalle.data.model.Dislike
import com.sgomez.navegaciondetalle.data.model.Favorito
import com.sgomez.navegaciondetalle.model.MediaItem
import kotlinx.coroutines.launch

@Composable
fun ListaScreen(viewModel: ListaViewModel, auth: AuthManager, navigateToDetail: (String) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    val lista by viewModel.lista.observeAsState(emptyList())
    val favoritosState = uiState.favoritos
    val dislikesState = uiState.dislikes
    val coroutineScope = rememberCoroutineScope()

    fun toggleFavorito(name:String) {
        coroutineScope.launch {
            val userId = auth.getCurrentUser()?.uid

            // Obtener la lista actual de favoritos
            val favoritosActuales = favoritosState.toMutableList()

            // Buscar si el favorito ya está en la lista
            val favoritoExistente = favoritosActuales.find { it.nombre == name }

            if (favoritoExistente != null) {
                // Si ya existe, eliminarlo de Firestore y de la lista
                viewModel.deleteFavoritoById(favoritoExistente.id)
                favoritosActuales.remove(favoritoExistente)
            } else {
                // Si no existe, crearlo y agregarlo a Firestore
                val nuevoFavorito = Favorito(nombre = name, userId = userId, favorito = true)
                viewModel.addFavorito(nuevoFavorito)

                // Obtener el favorito recién agregado de Firestore con el ID generado
                val favoritoNuevo = viewModel.getFavoritoByNombre(name, userId.toString())
                favoritoNuevo?.let { favoritosActuales.add(it) }
            }

            // Actualizar la lista de favoritos en la UI
            viewModel.updateFavoritos(favoritosActuales)
        }
    }

    fun toggleDislike(name:String) {
        coroutineScope.launch {
            val userId = auth.getCurrentUser()?.uid

            // Obtener la lista actual de dislikes
            val dislikesActuales = dislikesState.toMutableList()

            // Buscar si el dislike ya está en la lista
            val dislikeExistente = dislikesActuales.find { it.nombre == name }

            if (dislikeExistente != null) {
                // Si ya existe, eliminarlo de Firestore y de la lista
                viewModel.deleteDislikeById(dislikeExistente.id)
                dislikesActuales.remove(dislikeExistente)
            } else {
                // Si no existe, crearlo y agregarlo a Firestore
                val nuevoDislike = Dislike(nombre = name, userId = userId, dislike = true)
                viewModel.addDislike(nuevoDislike)

                // Obtener el favorito recién agregado de Firestore con el ID generado
                val dislikeNuevo = viewModel.getDislikeByNombre(name, userId.toString())
                dislikeNuevo?.let { dislikesActuales.add(it) }
            }

            // Actualizar la lista de favoritos en la UI
            viewModel.updateDislikes(dislikesActuales)
        }
    }

    if(uiState.isLoading || uiState.isLoadingFavoritos || uiState.isLoadingDislikes){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        if (lista!!.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay elementos", style = MaterialTheme.typography.bodySmall)
            }
        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                //modifier = Modifier.fillMaxSize()
            ) {
                items(lista!!) { mediaItem ->
                    MediaListItem(mediaItem, navigateToDetail, favoritosState, ::toggleFavorito, dislikesState, ::toggleDislike)
                }
            }
        }
    }

}

@Composable
private fun MediaListItem(
    mediaItem: MediaItem,
    navigateToDetail: (String) -> Unit,
    favoritosState: List<Favorito>,
    toggleFavorito: (String) -> Unit,
    dislikesState: List<Dislike>,
    toggleDislike: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Imagen(item = mediaItem,navigateToDetail = navigateToDetail)
        Title(item = mediaItem,  favoritosState = favoritosState, toggleFavorito = toggleFavorito, dislikesState = dislikesState , toggleDislike = toggleDislike)
    }
}

@Composable
fun Imagen(item: MediaItem, modifier: Modifier = Modifier,navigateToDetail: (String) -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { navigateToDetail(item.name) },
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = item.img,
                imageLoader = ImageLoader.Builder(context).crossfade(true).build()
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
//        if (item.tipo == Type.VIDEO) {
//            Icon(
//                imageVector = Icons.Default.PlayCircleOutline,
//                contentDescription = null,
//                tint = Color.White,
//                modifier = Modifier
//                    .size(92.dp)
//                    .align(Alignment.Center)
//            )
//        }

    }
}

@Composable
fun Title(item: MediaItem,favoritosState: List<Favorito>, toggleFavorito: (String) -> Unit, dislikesState: List<Dislike>, toggleDislike: (String) -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Cyan)
            .padding(16.dp)
    ) {
        Row{
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis,
            )

            IconButton(onClick = { toggleFavorito(item.name) }) {
                // Verificar si el elemento está en la lista de favoritos
                val esFavorito = favoritosState.any { it.nombre == item.name }
                val icon: ImageVector = if (esFavorito) {
                    Icons.Filled.Favorite  // Corazón relleno
                } else {
                    Icons.Outlined.FavoriteBorder  // Corazón vacío
                }
                Icon(imageVector = icon, contentDescription = "Favorito", tint = Color.Red)
            }

            IconButton(onClick = { toggleDislike(item.name) }) {
                val esDislike = dislikesState.any { it.nombre == item.name }
                // Cambiar el icono de acuerdo al estado de dislike
                val icon: ImageVector = Icons.Filled.Details
                val color: Color = if (esDislike) {
                    Color.Black
                } else {
                    Color.LightGray
                }
                Icon(imageVector = icon, contentDescription = "Dislike",tint = color)
            }
        }
    }
}


