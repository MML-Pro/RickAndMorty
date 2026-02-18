package com.example.rickandmorty.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.models.CharacterModel
import com.example.domain.models.CharacterResponse
import com.example.domain.models.CharacterStatus
import com.example.rickandmorty.ui.component.CharacterListItem
import com.example.rickandmorty.ui.component.DataPoint
import com.example.rickandmorty.ui.component.SimpleToolBar
import com.example.rickandmorty.ui.theme.RickAction
import com.example.rickandmorty.ui.theme.RickPrimary
import com.example.rickandmorty.ui.utils.Resource

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onCharacterClick: (Int) -> Unit = {}
) {
    val searchTextFieldState = searchViewModel.searchTextFieldState
    val filterState = searchViewModel.filterState.collectAsStateWithLifecycle()
    val originalResults = searchViewModel.originalResults.collectAsStateWithLifecycle()
    val searchResults = searchViewModel.searchResults.collectAsStateWithLifecycle()
    val searchTextState = searchViewModel.searchTextState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        val job = searchViewModel.observeSearchQuery()
        onDispose {
            job.cancel()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleToolBar(title = "Search")

        // شريط البحث
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = RickPrimary
                )
                BasicTextField(searchTextFieldState, modifier = Modifier.weight(1f))
            }

            AnimatedVisibility(visible = searchTextFieldState.text.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    tint = RickAction,
                    modifier = Modifier.clickable {
                        searchViewModel.searchTextFieldState.edit {
                            delete(0, searchTextFieldState.text.length)
                        }
                    }
                )
            }
        }

        // النص التوضيحي - يعرض العدد الكلي أو رسالة الانتظار
        if (originalResults.value is Resource.Success) {
            val total = (originalResults.value as Resource.Success).data.results.size
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = "$total results for '${searchTextFieldState.text}'",
                color = Color.White,
                fontSize = 14.sp,
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                text = searchTextState.value,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )
        }

        // أزرار الفلترة - تحسب الأعداد مباشرة من النتائج الأصلية
        FilterChipsRow(
            filterState = filterState.value,
            originalResults = originalResults.value,
            onStatusClick = { status ->
                searchViewModel.toggleStatusFilter(status)
            }
        )

        // نتائج البحث
        when (val state = searchResults.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RickPrimary)
                }
            }

            is Resource.Success -> {
                val characters = state.data.results
                if (characters.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No characters found",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    SearchScreenContent(characters, onCharacterClick)
                }
            }

            is Resource.Error -> {
                Text(
                    text = state.message,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp
                )
            }

            else -> {}
        }
    }
}

@Composable
fun SearchScreenContent(content: List<CharacterModel>, onCharacterClicked:(Int)-> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.clipToBounds()
    ) {
        items(content, key = { it.id }) { character ->
            val dataPoints = buildList {
                add(DataPoint("Last Known Location", character.location.name))
                add(DataPoint("Species", character.species))
                add(DataPoint("Gender", character.gender.displayName))
                character.type.takeIf {
                    it.isNotBlank()
                }?.let {
                    add(DataPoint("Type", it))
                }
                add(DataPoint("Origin", character.origin.name))
                add(DataPoint("Episode Count", character.episodeIds.size.toString()))
            }

            CharacterListItem(
                character = character,
                characterDataPoints = dataPoints,
                onItemClick = {
                    onCharacterClicked(character.id)
                },
                modifier = Modifier.animateItem()
            )
        }
    }
}

// مكون أزرار الفلترة - يستقبل النتائج الأصلية ويحسب العدد داخله
@Composable
private fun FilterChipsRow(
    filterState: Resource.FilterState,
    originalResults: Resource<CharacterResponse>,
    onStatusClick: (CharacterStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filterState.statuses.forEach { status ->
            val isSelected = filterState.selectedStatus.contains(status)
            val contentColor = if (isSelected) RickAction else Color.LightGray

            // حساب العدد مباشرة هنا بدلاً من ViewModel
            val count = if (originalResults is Resource.Success) {
                originalResults.data.results.filter { it.status == status }.size
            } else {
                0
            }

            Row(
                modifier = Modifier
                    .border(1.dp, color = contentColor, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onStatusClick(status) }
            ) {
                Text(
                    text = count.toString(),
                    color = RickPrimary,
                    modifier = Modifier
                        .background(contentColor)
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )

                Text(
                    text = status.displayName,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
    }
}