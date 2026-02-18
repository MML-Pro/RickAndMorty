package com.example.rickandmorty.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.CharacterGender
import com.example.domain.models.CharacterModel
import com.example.domain.models.CharacterStatus
import com.example.domain.models.Location
import com.example.domain.models.Origin
import com.example.rickandmorty.ui.theme.RickAction

@Composable
fun CharacterGridItem(
    modifier: Modifier,
    characterModel: CharacterModel,
    onItemClick: () -> Unit
) {

    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(colors = listOf(Color.Transparent, RickAction)),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onItemClick()
            }
    ) {

        Box() {
            CharacterImage(imageUrl = characterModel.image)

            CharacterStatusDot(
                characterModel.status,
                modifier = Modifier.padding(start = 6.dp, top = 6.dp)
            )
        }

        Text(
            text = characterModel.name,
            color = RickAction,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 26.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )

    }

}


@Preview(showBackground = true)
@Composable
fun CharacterGridItemPreview() {
    val characterModel = CharacterModel(
        created = "2017-11-04T18:48:46.250Z",
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.Alive,
        species = "Human",
        type = "",
        gender = CharacterGender.Male,
        origin = Origin(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
        location = Location(
            name = "Citadel of Ricks",
            url = "https://rickandmortyapi.com/api/location/3"
        ),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        url = "https://rickandmortyapi.com/api/character/1",
        episodeIds = listOf(1, 2, 3)
    )
    CharacterGridItem(modifier = Modifier, characterModel = characterModel) {

    }
}