package com.sgomez.navegaciondetalle.ui.screen.ListaScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sgomez.navegaciondetalle.data.FirestoreManager
import com.sgomez.navegaciondetalle.data.model.Dislike
import com.sgomez.navegaciondetalle.data.model.Favorito
import com.sgomez.navegaciondetalle.model.MediaItem
import com.sgomez.navegaciondetalle.data.repositories.RemoteConectecition
import com.sgomez.navegaciondetalle.data.repositories.model.Result
import com.sgomez.navegaciondetalle.data.repositories.model.toMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListaViewModel(val firestoreManager: FirestoreManager): ViewModel() {
    private val _lista: MutableLiveData<List<MediaItem>> = MutableLiveData()
    val lista: LiveData<List<MediaItem>> = _lista

    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        _uiState.update { it.copy(isLoading = true,isLoadingFavoritos = true,isLoadingDislikes = true) }

        viewModelScope.launch() {
            val digimons = RemoteConectecition.service.getDigimonAll()
            _lista.value = digimons.map {
                val result = Result(it.name,it.img,it.level)
                result.toMediaItem()
            }
            _uiState.update { it.copy(isLoading = false) }

        }

        viewModelScope.launch {
            firestoreManager.getFavoritos().collect { lista ->
                _uiState.update { uiState ->
                    uiState.copy(
                        favoritos = lista,
                        isLoadingFavoritos = false
                    )
                }
            }
        }

        viewModelScope.launch {
            firestoreManager.getDislike().collect { lista ->
                _uiState.update { uiState ->
                    uiState.copy(
                        dislikes = lista,
                        isLoadingDislikes = false
                    )
                }
            }
        }
    }

    fun updateFavoritos(nuevosFavorito: List<Favorito>) {
        _uiState.update { it.copy(favoritos = nuevosFavorito) }
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


    fun updateDislikes(nuevosDislikes: List<Dislike>) {
        _uiState.update { it.copy(dislikes = nuevosDislikes) }
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
    val dislikes: List<Dislike> = emptyList(),
    val favoritos: List<Favorito> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingFavoritos: Boolean = false,
    val isLoadingDislikes: Boolean = false
)

class ListaViewModelFactory(private val firestoreManager: FirestoreManager): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListaViewModel(firestoreManager) as T
    }
}