package com.example.rickandmorty.ui.utils

import retrofit2.HttpException
import java.io.IOException

fun Throwable.toResourceError(): Resource.Error {
    return when (this) {
        is HttpException -> {
            Resource.Error(
                code = this.code(),
                message = when (this.code()) {
                    404 -> "Character not found"
                    500 -> "Internal server error"
                    else -> "HTTP ${this.code()}: ${this.message()}"
                },
                body = this.response()?.errorBody()?.string()
            )
        }
        is IOException -> {
            Resource.Error(
                message = "Network error, check your connection"
            )
        }
        else -> {
            Resource.Error(
                message = "Unexpected error: ${this.localizedMessage ?: "Unknown"}"
            )
        }
    }
}
