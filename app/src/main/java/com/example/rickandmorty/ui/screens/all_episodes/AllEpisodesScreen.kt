// ===== GetAllEpisodes.kt - الحل البسيط =====
package com.example.rickandmorty.ui.screens.all_episodes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rickandmorty.ui.screens.all_episodes.AllEpisodesViewModel
import com.example.rickandmorty.ui.component.EpisodeRowComponent
import com.example.rickandmorty.ui.component.LoadingState
import com.example.rickandmorty.ui.component.SimpleToolBar
import com.example.rickandmorty.ui.theme.RickAction
import com.example.rickandmorty.ui.theme.RickPrimary
import com.example.rickandmorty.ui.utils.Resource

@Composable
fun AllEpisodesScreen(allEpisodesViewModel: AllEpisodesViewModel) {

    val episodesResource by allEpisodesViewModel.episodes.collectAsState()

    LaunchedEffect(Unit) {
        allEpisodesViewModel.getAllEpisodes()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleToolBar(title = "All Episodes")

        when (val resource = episodesResource) {
            Resource.Initial -> {
                // حالة أولية
            }

            Resource.Loading -> {
                LoadingState()
            }

            is Resource.Success -> {
                // ✅ دلوقتي resource.data هو Map<String, List<EpisodeModel>>
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    resource.data.forEach { (seasonName, episodes) ->

                        // عنوان الموسم
                        stickyHeader (key = seasonName) {
                            Column (modifier = Modifier.fillMaxWidth().background(color = RickPrimary)){
                                Text(
                                    text = seasonName,
                                    color = Color.Red,
                                    fontSize = 32.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                // احسب عدد الشخصيات الفريدة في الموسم
                                val uniqueCharactersCount = episodes
                                    .flatMap { it.characters } // اجمع كل الشخصيات من كل الحلقات
                                    .distinct() // خد الشخصيات الفريدة بس
                                    .size

                                Text(
                                    text = "$uniqueCharactersCount characters",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                )

                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .height(4.dp)
                                        .background(
                                            color = RickAction,
                                            shape = RoundedCornerShape(2.dp)
                                        ),

                                    )
                            }
                        }

                        // حلقات الموسم
                        items(
                            items = episodes,
                            key = { episode -> episode.id }
                        ) { episode ->
                            EpisodeRowComponent(episode = episode)
                        }
                    }
                }
            }

            is Resource.Error -> {
                ErrorMessage(
                    message = resource.message,
                    onRetry = { allEpisodesViewModel.getAllEpisodes() }
                )
            }

            Resource.Empty -> {
                EmptyMessage()
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Error: $message")
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No episodes found")
    }
}