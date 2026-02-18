package com.example.rickandmorty.ui.screens.search

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.CharacterResponse
import com.example.domain.models.CharacterStatus
import com.example.domain.usecase.SearchAllCharactersByNameUseCase
import com.example.rickandmorty.ui.utils.Resource
import com.example.rickandmorty.ui.utils.toResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchAllCharactersByNameUseCase: SearchAllCharactersByNameUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    // النتائج الأصلية من API (بدون فلترة) - public حتى يستطيع الـ UI حساب الأعداد منها
    private val _originalSearchResults = MutableStateFlow<Resource<CharacterResponse>>(Resource.Initial)
    val originalResults: StateFlow<Resource<CharacterResponse>> = _originalSearchResults

    // النتائج المعروضة (بعد الفلترة)
    private val _searchResults = MutableStateFlow<Resource<CharacterResponse>>(Resource.Initial)
    val searchResults: StateFlow<Resource<CharacterResponse>> = _searchResults

    val searchTextFieldState = TextFieldState()

    // حالة الفلترة - تحتوي على جميع الحالات المتاحة والمختارة
    private val _filterState = MutableStateFlow(
        Resource.FilterState(
            statuses = listOf(
                CharacterStatus.Alive,
                CharacterStatus.Dead,
                CharacterStatus.Unknown
            ),
            selectedStatus = emptyList()
        )
    )
    val filterState: StateFlow<Resource.FilterState> = _filterState

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchTextState = snapshotFlow { searchTextFieldState.text }
        .debounce(500)
        .mapLatest { text ->
            if (text.isBlank()) {
                "Awaiting your command ..."
            } else {
                "Searching for $text"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Awaiting your command ..."
        )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun observeSearchQuery() = viewModelScope.launch {
        snapshotFlow { searchTextFieldState.text }
            .debounce(500)
            .mapLatest { it.toString() }
            .collect { query ->
                if (query.isNotBlank()) {
                    searchAllCharacters(query)
                } else {
                    _originalSearchResults.value = Resource.Initial
                    _searchResults.value = Resource.Initial
                }
            }
    }

    // ✅ دالة البحث - تحفظ النتائج الأصلية ثم تطبق الفلترة
    private fun searchAllCharacters(query: String) {
        _searchResults.value = Resource.Loading

        viewModelScope.launch {
            try {
                val result = searchAllCharactersByNameUseCase(query)
                _originalSearchResults.value = Resource.Success(result)
                applyFilter()
                Log.d(TAG, "Search successful: ${result.results.size} characters found")
            } catch (e: Exception) {
                Log.e(TAG, "searchAllCharacters error", e)
                _searchResults.value = e.toResourceError()
                _originalSearchResults.value = e.toResourceError()
            }
        }
    }

    fun toggleStatusFilter(status: CharacterStatus) {
        val currentSelected = _filterState.value.selectedStatus
        val newSelected = if (currentSelected.contains(status)) {
            // إذا كانت موجودة، نحذفها (unselect)
            currentSelected - status
        } else {
            // إذا لم تكن موجودة، نضيفها (select)
            currentSelected + status
        }

        _filterState.value = _filterState.value.copy(selectedStatus = newSelected)

        // ✅ إعادة تطبيق الفلترة بعد التغيير
        applyFilter()
    }

    // ✅ دالة تطبيق الفلترة على النتائج الأصلية
    private fun applyFilter() {
        val originalResults = _originalSearchResults.value

        if (originalResults is Resource.Success) {
            val selectedStatuses = _filterState.value.selectedStatus

            // ✅ إذا لم يتم اختيار أي فلتر، نعرض كل النتائج
            val filteredCharacters = if (selectedStatuses.isEmpty()) {
                originalResults.data.results
            } else {
                // ✅ نعرض فقط الشخصيات التي تطابق الحالات المختارة
                originalResults.data.results.filter { character ->
                    selectedStatuses.contains(character.status)
                }
            }

            // ✅ تحديث النتائج المعروضة
            _searchResults.value = Resource.Success(
                originalResults.data.copy(results = filteredCharacters)
            )
        }
    }

    // ✅ دالة لإعادة تعيين جميع الفلاتر
    fun clearAllFilters() {
        _filterState.value = _filterState.value.copy(selectedStatus = emptyList())
        applyFilter()
    }
}