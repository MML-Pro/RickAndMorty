package com.example.rickandmorty.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.CharacterModel
import com.example.domain.models.CharacterResponse
import com.example.domain.usecase.GetCharacterByIdUseCase
import com.example.rickandmorty.ui.utils.Resource
import com.example.rickandmorty.ui.utils.toResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase
) : ViewModel() {

    companion object { private const val TAG = "CharacterViewModel" }

    private val _character = MutableStateFlow<Resource<CharacterModel>>(Resource.Initial)
    val character: StateFlow<Resource<CharacterModel>> = _character

    private var lastLoadedId: Int? = null

    fun getCharacterById(id: Int, forceRefresh: Boolean = false) {
        // ✅ لو نفس الشخصية موجودة بالفعل Success، ما تعملش Loading ولا refetch
        val current = _character.value
        if (!forceRefresh && lastLoadedId == id && current is Resource.Success) return

        lastLoadedId = id
        _character.value = Resource.Loading

        viewModelScope.launch {
            try {
                val response = getCharacterByIdUseCase(id)
                _character.value = Resource.Success(response)
            } catch (e: Exception) {
                Log.e(TAG, "getCharacterById error", e)
                _character.value = e.toResourceError()
            }
        }
    }
}
