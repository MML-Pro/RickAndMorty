package com.example.rickandmorty.ui.screens.character_episode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.CharacterModel
import com.example.domain.models.EpisodeModel
import com.example.rickandmorty.ui.EpisodeViewModel
import com.example.rickandmorty.ui.component.CharacterImage
import com.example.rickandmorty.ui.component.CharacterNameComponent
import com.example.rickandmorty.ui.component.DataPoint
import com.example.rickandmorty.ui.component.DataPointComponent
import com.example.rickandmorty.ui.component.EpisodeRowComponent
import com.example.rickandmorty.ui.component.ErrorScreen
import com.example.rickandmorty.ui.component.LoadingState
import com.example.rickandmorty.ui.theme.RickPrimary
import com.example.rickandmorty.ui.theme.RickTextPrimary
import com.example.rickandmorty.ui.utils.Resource


@Composable
fun CharacterEpisodeScreen(
    character: CharacterModel,
    episodeIds: List<Int>,
    episodeViewModel: EpisodeViewModel,
    listState: LazyListState
) {
    val episodesState by episodeViewModel.episodes.collectAsState()

    LaunchedEffect(episodeIds) {
        episodeViewModel.getEpisodes(episodeIds)
    }

    when (val eState = episodesState) {
        is Resource.Initial, is Resource.Loading -> LoadingState()
        is Resource.Error -> ErrorScreen(eState.message)
        Resource.Empty -> Text("No episodes")
        is Resource.Success -> MainScreen(character, eState.data,listState) // 🟢 نمرر الشخصية + الحلقات


    }

}

@Composable
private fun MainScreen(
    characterModel: CharacterModel,
    episodesList: List<EpisodeModel>,
    listState: LazyListState        // ← باراميتر جديد
) {


    val episodeBySeasonMap by remember {
        mutableStateOf(episodesList.groupBy { it.seasonNumber })
    }



    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.Top,
        state = listState
    ) {
        item {
            CharacterNameComponent(name = characterModel.name)
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LazyRow {
                episodeBySeasonMap.forEach { mapEntry ->

                    val title = "Season: ${mapEntry.key} "

                    val description = "${mapEntry.value.size} ep"

                    item {
                        DataPointComponent(DataPoint(title, description))
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            CharacterImage(imageUrl = characterModel.image)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        episodeBySeasonMap.forEach { mapEntry ->

            stickyHeader { SeasonHeader(mapEntry.key) }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(mapEntry.value) {
                EpisodeRowComponent(episode = it)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

}

@Composable
private fun SeasonHeader(seasonNumber: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RickPrimary)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Season $seasonNumber",
            color = RickTextPrimary,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = RickTextPrimary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        )
    }
}
