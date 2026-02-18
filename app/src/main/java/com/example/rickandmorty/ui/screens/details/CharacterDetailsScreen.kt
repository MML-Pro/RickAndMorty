package com.example.rickandmorty.ui.screens.details

//import com.example.rickandmorty.ui.component.DataPointComponent
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rickandmorty.ui.CharacterViewModel
import com.example.rickandmorty.ui.component.CharacterDetailsNamePlateComponent
import com.example.rickandmorty.ui.component.DataPoint
import com.example.rickandmorty.ui.component.DataPointComponent
import com.example.rickandmorty.ui.component.ErrorScreen
import com.example.rickandmorty.ui.component.LoadingState
import com.example.rickandmorty.ui.component.SimpleToolBar
import com.example.rickandmorty.ui.theme.RickAction
import com.example.rickandmorty.ui.utils.Resource

private const val TAG = "CharacterDetailsScreen"

@Composable
fun CharacterDetailsScreen(
    viewModel: CharacterViewModel,
    characterId: Int,
    episodesClicked: (List<Int>) -> Unit,
    onBackClicked : ()-> Unit
) {
    val characterResource by viewModel.character.collectAsState()



    LaunchedEffect(characterId) {
//        if (viewModel.character.value !is Resource.Success) {
//            viewModel.getCharacterById(characterId)
//        }

        viewModel.getCharacterById(characterId)
    }


    when (characterResource) {
        is Resource.Initial, is Resource.Loading -> {
            Column(modifier = Modifier.fillMaxSize()) {
                LoadingState()
            }
        }

        is Resource.Error -> {
            val msg = (characterResource as Resource.Error).message
            ErrorScreen(msg)
        }

        is Resource.Success -> {

            val character = (characterResource as Resource.Success).data // ✅ شخصية واحدة


            Log.d(TAG, "CharacterDetailsScreen: ${character.name}")

            val characterDataPoint = remember(character) {
                buildList {
                    add(DataPoint("Last Known Location", character.location.name))
                    add(DataPoint("Species", character.species))
                    add(DataPoint("Gender", character.gender.displayName))
                    character.type.takeIf { it.isNotEmpty() }?.let { type ->
                        add(DataPoint("Type", type))
                    }
                    add(DataPoint("Origin", character.origin.name))
                    add(DataPoint("Episode count", character.episodeIds.size.toString()))
                }
            }

            Log.d(TAG, "CharacterDetailsScreen: ${character.status.toString()}")




            Column {
                SimpleToolBar(title = "Character Details"){

                    onBackClicked()
                }


                LazyColumn(
                    modifier = Modifier.fillMaxSize().clipToBounds(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    item {
                        CharacterDetailsNamePlateComponent(
                            name = character.name,
                            status = character.status,
                        )
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        AsyncImage(
                            model = character.image,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    items(characterDataPoint) {
                        Spacer(modifier = Modifier.height(32.dp))
                        DataPointComponent(dataPoint = it)
                    }
//
                    item { Spacer(modifier = Modifier.height(32.dp)) }

                    item {
                        Text(
                            text = "View All Episodes",
                            color = RickAction,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .border(1.dp, RickAction, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    episodesClicked(character.episodeIds) // ✅ هنا عندك List<Int>
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Resource.Empty -> Text("No character found")
    }
}
