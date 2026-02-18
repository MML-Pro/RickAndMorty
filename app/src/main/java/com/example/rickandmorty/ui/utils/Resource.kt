package com.example.rickandmorty.ui.utils

import com.example.domain.models.CharacterStatus

//sealed class Resource<T>(
//    val data: T? = null,
//    val message: String? = null
//) {
//
//    class Ideal<T> : Resource<T>()
//
//    class Success<T>(data: T?) : Resource<T>(data)
//
//    class Error<T>(message: String?) : Resource<T>(message = message)
//
//    class Loading<T> : Resource<T>()
//}


sealed class Resource<out T> {
    object Initial : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(
        val code: Int? = null,
        val message: String,
        val body: String? = null
    ) : Resource<Nothing>()

    object Empty : Resource<Nothing>() // حالة جديدة للقوائم الفارغة

    data class FilterState(
        val statuses: List<CharacterStatus>,
        val selectedStatus: List<CharacterStatus>
    )
}