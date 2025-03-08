package com.sgomez.navegaciondetalle.ui.screen.DetailScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sgomez.navegaciondetalle.data.AuthManager
import com.sgomez.navegaciondetalle.data.FirestoreManager
import com.sgomez.navegaciondetalle.data.model.Dislike
import com.sgomez.navegaciondetalle.data.model.Favorito
import com.sgomez.navegaciondetalle.model.MediaItem
import com.sgomez.navegaciondetalle.data.repositories.RemoteConectecition
import com.sgomez.navegaciondetalle.data.repositories.model.Result
import com.sgomez.navegaciondetalle.data.repositories.model.toMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(val firestoreManager: FirestoreManager,name: String,auth: AuthManager): ViewModel() {
    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init{
        val userId =auth.getCurrentUser()?.uid.toString()

        viewModelScope.launch {
            try {
                val favorito = firestoreManager.getFavoritoByNombre(name, userId)
                _uiState.update { it.copy(favorito = favorito) }
            } catch (e: Exception) {
                Log.e("updateFavoritoDislike", "Error obteniendo favorito: ${e.message}", e)
            }
        }

        viewModelScope.launch {
            try {
                val dislike = firestoreManager.getDislikeByNombre(name, userId)
                _uiState.update { it.copy(dislike = dislike) }
            } catch (e: Exception) {
                Log.e("updateFavoritoDislike", "Error obteniendo dislike: ${e.message}", e)
            }
        }
    }

    fun updateFavorito(nuevoFavorito: Favorito?) {
        _uiState.update { it.copy(favorito = nuevoFavorito) }
    }

    fun addFavorito(favorito: Favorito) {
        viewModelScope.launch {
            firestoreManager.addFavorito(favorito)
        }
    }
    fun deleteFavoritoById(favoritoId: String?) {
        if (favoritoId.isNullOrEmpty()) return
        viewModelScope.launch {
            firestoreManager.deleteFavoritoById(favoritoId)
        }
    }
    suspend fun getFavoritoByNombre(name: String,userId:String):Favorito? {
        return firestoreManager.getFavoritoByNombre(name, userId)
    }


    fun updateDislike(nuevoDislike: Dislike?) {
        _uiState.update { it.copy(dislike = nuevoDislike) }
    }

    fun addDislike(dislike: Dislike) {
        viewModelScope.launch {
            firestoreManager.addDislike(dislike)
        }
    }
    fun deleteDislikeById(dislikeId: String?) {
        if (dislikeId.isNullOrEmpty()) return
        viewModelScope.launch {
            firestoreManager.deleteDislikeById(dislikeId)
        }
    }
    suspend fun getDislikeByNombre(name: String,userId:String):Dislike? {
        return firestoreManager.getDislikeByNombre(name, userId)
    }
}

data class UiState(
    val dislike: Dislike? = null,
    val favorito: Favorito? = null
)

class DetailViewModelFactory(private val firestoreManager: FirestoreManager,private val name: String,private val auth: AuthManager): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(firestoreManager,name,auth) as T
    }
}