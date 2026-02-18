package com.example.rickandmorty.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.EpisodeModel
import com.example.rickandmorty.ui.theme.RickPrimary
import com.example.rickandmorty.ui.theme.RickTextPrimary


@Composable
fun EpisodeRowComponent(
    episode: EpisodeModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(
            color = RickPrimary
        )
    ) {
        DataPointComponent(
            DataPoint(
                title = "Episode",
                description = episode.episodeNumber.toString()
            )
        )

        Spacer(modifier = Modifier.width(64.dp))

        Column() {
            Text(
                text = episode.name,
                fontSize = 24.sp,
                color = RickTextPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = episode.airDate,
                fontSize = 16.sp,
                color = RickTextPrimary,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun EpisodeRowComponentPreview() {
    EpisodeRowComponent(
        episode = EpisodeModel(
            id = 1,
            name = "Pilot",
            seasonNumber = 1,
            episodeNumber = 1,
            airDate = "December 2, 2013",
            characters = emptyList()

        )
    )
}