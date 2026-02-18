package com.example.rickandmorty.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.CharacterResponse
import com.example.domain.usecase.GetCharacterByPageUseCase
import com.example.rickandmorty.ui.component.CharacterGridItem
import com.example.rickandmorty.ui.component.ErrorScreen
import com.example.rickandmorty.ui.component.LoadingState
import com.example.rickandmorty.ui.component.SimpleToolBar
import com.example.rickandmorty.ui.utils.Resource
import com.example.rickandmorty.ui.utils.toResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//sealed interface HomeScreenViewState {
//
//    object Loading : HomeScreenViewState
//
//    data class GridDisplay(
//        val characters: List<CharacterModel> = emptyList()
//    ) : HomeScreenViewState
//}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val getCharacterByPageUseCase: GetCharacterByPageUseCase) :
    ViewModel() {

    companion object {
        private const val TAG = "HomeScreen"
    }

    private val _characters = MutableStateFlow<Resource<CharacterResponse>>(Resource.Initial)
    val characters: StateFlow<Resource<CharacterResponse>> = _characters.asStateFlow()

//    private val _viewState = MutableStateFlow<HomeScreenViewState>(HomeScreenViewState.Loading)
//    val viewState: StateFlow<HomeScreenViewState> = _viewState.asStateFlow()

    private val fetchedCharacterPages = mutableListOf<CharacterResponse>()

    fun fetchInitialPage(page: Int) {

        viewModelScope.launch {

            if (fetchedCharacterPages.isNotEmpty()) {
                // ✅ رجّع البيانات اللي معاك بدل ما تسيبها على Loading
                val allCharacters = fetchedCharacterPages.flatMap { it.results }
                _characters.value = Resource.Success(
                    CharacterResponse(
                        info = fetchedCharacterPages.last().info,
                        results = allCharacters
                    )
                )
                return@launch
            }

            _characters.value = Resource.Loading

            try {
                val response = getCharacterByPageUseCase(page)
                _characters.value = Resource.Success(response)
            } catch (e: Exception) {
                Log.e(TAG, "getCharacters error", e)
                _characters.value = e.toResourceError()
            }
        }
    }


  fun fetchNextPage() {
    val nextPageIndex = fetchedCharacterPages.size + 1

    viewModelScope.launch {
        try {
            val response = getCharacterByPageUseCase(nextPageIndex)
            fetchedCharacterPages.add(response)

            // 🟢 دمج النتائج القديمة مع الجديدة
            val allCharacters = fetchedCharacterPages.flatMap { it.results }
            val mergedResponse = CharacterResponse(
                info = response.info,
                results = allCharacters
            )
            _characters.value = Resource.Success(mergedResponse)

        } catch (e: Exception) {
            Log.e(TAG, "fetchNextPage error", e)
            _characters.value = e.toResourceError()
        }
    }
}

}


@Composable
fun HomeScreen(viewModel: HomeScreenViewModel, onCharacterSelected: (Int) -> Unit) {

    val charactersResource by viewModel.characters.collectAsState()


    LaunchedEffect(viewModel) {
        viewModel.fetchInitialPage(1)
    }

    val listState = rememberLazyGridState()

    val fetchNextPage: Boolean by remember {
        derivedStateOf {
            val currentCharacterCount = (charactersResource as? Resource.Success)?.data?.results?.size ?: return@derivedStateOf false
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            return@derivedStateOf lastVisibleItem >= currentCharacterCount - 1

        }
    }

    LaunchedEffect( fetchNextPage) {
        if(fetchNextPage){
            viewModel.fetchNextPage()
        }
    }


    when (charactersResource) {
        is Resource.Initial, is Resource.Loading -> {
            LoadingState()
        }

        is Resource.Error -> {
            val msg = (charactersResource as Resource.Error).message
            ErrorScreen(msg)
        }

        is Resource.Success -> {
            val characters = (charactersResource as Resource.Success).data.results

            Column {

                SimpleToolBar("All Characters")

                LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)

                ) {

                    items(characters, key = { it.id }) { character ->
                        CharacterGridItem(modifier = Modifier, characterModel = character) {
                            onCharacterSelected(character.id)
                        }
                    }

                }
            }
        }

        Resource.Empty -> Text("No character found")

    }


}