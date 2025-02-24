package com.sgomez.navegaciondetalle.data

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.sgomez.navegaciondetalle.data.model.Dislike
import com.sgomez.navegaciondetalle.data.model.DislikeDB
import com.sgomez.navegaciondetalle.data.model.Favorito
import com.sgomez.navegaciondetalle.data.model.FavoritoDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreManager(auth: AuthManager, context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = auth.getCurrentUser()?.uid

    companion object {
        const val APPDIGIMON_FAVORITO_COLLECTION = "appDigimon_favorito"
        const val APPDIGIMON_DISLIKE_COLLECTION = "appDigimon_dislike"
    }

    //****************************************************************************
    // FAVORITOS
    //****************************************************************************
    fun getFavoritos(): Flow<List<Favorito>> {
        return firestore.collection(APPDIGIMON_FAVORITO_COLLECTION)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { qs ->
                qs.documents.mapNotNull { ds ->
                    ds.toObject(FavoritoDB::class.java)?.let { favoritoDB ->
                        Favorito(
                            id = ds.id,
                            userId = favoritoDB.userId,
                            favorito = favoritoDB.favorito,
                            nombre = favoritoDB.nombre
                        )
                    }
                }
            }
    }

    suspend fun getFavoritoByNombre(nombre: String,userId: String): Favorito? {
        return try {
            val querySnapshot = firestore.collection(APPDIGIMON_FAVORITO_COLLECTION)
                .whereEqualTo("nombre", nombre)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()

            document?.toObject(FavoritoDB::class.java)?.let {
                Favorito(
                    id = document.id,
                    userId = it.userId,
                    favorito = it.favorito,
                    nombre = it.nombre
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addFavorito(favorito: Favorito){
        firestore.collection(APPDIGIMON_FAVORITO_COLLECTION).add(favorito).await()
    }

    suspend fun deleteFavoritoById(favoritoId: String?) {
        if (favoritoId != null) {
            firestore.collection(APPDIGIMON_FAVORITO_COLLECTION).document(favoritoId).delete().await()
        }
    }

    suspend fun updateFavorito(favorito: Favorito?) {
        if (favorito != null) {
            val noteRef = favorito.id?.let {
                firestore.collection(APPDIGIMON_FAVORITO_COLLECTION).document(it)
            }
            noteRef?.set(favorito)?.await()
        }
    }


    //****************************************************************************
    // DISLIKE
    //****************************************************************************
    fun getDislike(): Flow<List<Dislike>> {
        return firestore.collection(APPDIGIMON_DISLIKE_COLLECTION)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { qs ->
                qs.documents.mapNotNull { ds ->
                    ds.toObject(DislikeDB::class.java)?.let { dislikeDB ->
                        Dislike(
                            id = ds.id,
                            userId = dislikeDB.userId,
                            dislike = dislikeDB.dislike,
                            nombre = dislikeDB.nombre
                        )
                    }
                }
            }
    }

    suspend fun getDislikeByNombre(nombre: String,userId: String): Dislike? {
        return try {
            val querySnapshot = firestore.collection(APPDIGIMON_DISLIKE_COLLECTION)
                .whereEqualTo("nombre", nombre)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()

            document?.toObject(DislikeDB::class.java)?.let {
                Dislike(
                    id = document.id,
                    userId = it.userId,
                    dislike = it.dislike,
                    nombre = it.nombre
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addDislike(dislike: Dislike){
        firestore.collection(APPDIGIMON_DISLIKE_COLLECTION).add(dislike).await()
    }

    suspend fun deleteDislikeById(dislikeId: String?) {
        if (dislikeId != null) {
            firestore.collection(APPDIGIMON_DISLIKE_COLLECTION).document(dislikeId).delete().await()
        }
    }

    suspend fun updateDislike(dislike: Dislike?) {
        if (dislike != null) {
            val noteRef = dislike.id?.let {
                firestore.collection(APPDIGIMON_DISLIKE_COLLECTION).document(it)
            }
            noteRef?.set(dislike)?.await()
        }
    }
}