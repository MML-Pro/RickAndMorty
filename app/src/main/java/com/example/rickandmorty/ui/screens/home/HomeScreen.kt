package com.example.rickandmorty.ui.screens.home

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getCharacterByPageUseCase: GetCharacterByPageUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "HomeScreen"
        private const val DEFAULT_RETRY_SECONDS = 2L
    }

    private val _characters = MutableStateFlow<Resource<CharacterResponse>>(Resource.Initial)
    val characters: StateFlow<Resource<CharacterResponse>> = _characters.asStateFlow()

    // ✅ Event للتوست (مرة واحدة فقط)
    private val _uiEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    private val fetchedCharacterPages = mutableListOf<CharacterResponse>()

    private var isPaging = false
    private var hasNext = true

    // ✅ cooldown الحقيقي: ممنوع طلبات قبل الوقت ده
    private var cooldownUntilMs: Long = 0L

    fun fetchInitialPage(page: Int) {
        viewModelScope.launch {
            _characters.value = Resource.Loading
            try {
                val response = getCharacterByPageUseCase(page)
                fetchedCharacterPages.clear()
                fetchedCharacterPages.add(response)
                hasNext = response.info.next != null
                _characters.value = Resource.Success(response)
            } catch (e: Exception) {
                Log.e(TAG, "fetchInitialPage error", e)
                _characters.value = e.toResourceError()
            }
        }
    }

    fun fetchNextPage() {
        if (isPaging || !hasNext) return

        // ✅ لو داخل cooldown: اقفل أي محاولات paging بدون ما تطلع توست تاني
        val now = System.currentTimeMillis()
        if (now < cooldownUntilMs) return

        val nextPageIndex = fetchedCharacterPages.size + 1
        isPaging = true

        viewModelScope.launch {
            try {
                val response = getCharacterByPageUseCase(nextPageIndex)
                fetchedCharacterPages.add(response)

                hasNext = response.info.next != null  // لو API بترجع next=null يبقى خلّصنا

                val allCharacters = fetchedCharacterPages.flatMap { it.results }
                _characters.value = Resource.Success(
                    CharacterResponse(info = response.info, results = allCharacters)
                )
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    val retryAfter = e.response()?.headers()?.get("Retry-After")?.toLongOrNull()
                    val waitSeconds = retryAfter ?: DEFAULT_RETRY_SECONDS

                    val newCooldownUntil = System.currentTimeMillis() + waitSeconds * 1000L

                    // ✅ اطّلع توست مرة واحدة فقط: وقت ما ندخل cooldown جديد
                    val wasNotInCooldown = System.currentTimeMillis() >= cooldownUntilMs
                    cooldownUntilMs = newCooldownUntil

                    if (wasNotInCooldown) {
                        _uiEvents.tryEmit("Scrolling too fast. Try again after $waitSeconds seconds.")
                    }

                    // ✅ (اختياري) لو حابب تمنع أي re-trigger قبل انتهاء المهلة بالكامل:
                    // delay(waitSeconds * 1000L)
                } else {
                    _characters.value = e.toResourceError()
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchNextPage error", e)
                _characters.value = e.toResourceError()
            } finally {
                isPaging = false
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onCharacterSelected: (Int) -> Unit
) {
    val charactersResource by viewModel.characters.collectAsState()
    val listState = rememberLazyGridState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchInitialPage(1)
    }

    // ✅ توست مرة واحدة فقط عن طريق Event collector
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * ✅ Trigger paging بطريقة تمنع تكرار الطلبات بسبب recomposition
     * - snapshotFlow + distinctUntilChanged + debounce
     */
    LaunchedEffect(listState) {
        snapshotFlow {
            val total = (charactersResource as? Resource.Success)?.data?.results?.size ?: 0
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && lastVisible >= total - 6
        }
            .distinctUntilChanged()
            .filter { it }
            .debounce(300)
            .collect {
                viewModel.fetchNextPage()
            }
    }

    when (charactersResource) {
        is Resource.Initial, is Resource.Loading -> LoadingState()

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
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
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