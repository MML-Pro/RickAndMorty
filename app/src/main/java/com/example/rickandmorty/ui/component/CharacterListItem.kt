package com.example.rickandmorty.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.models.CharacterModel
import com.example.rickandmorty.ui.theme.RickAction

@Composable
fun CharacterListItem(
    modifier: Modifier,
    character: CharacterModel,
    characterDataPoints: List<DataPoint>,
    onItemClick: () -> Unit
) {

    Row(
        modifier = modifier
            .height(140.dp)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(colors = listOf(Color.Transparent, RickAction)),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onItemClick() }
    ) {
        // صورة + حالة
        Box() {
            CharacterImage(
                imageUrl = character.image, modifier = Modifier

                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            )

            CharacterStatusDot(
                status = character.status,
                modifier = Modifier.padding(start = 6.dp, top = 6.dp)
            )
        }

        // Grid للـ DataPoints
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            modifier = Modifier
                .fillMaxWidth(),

            content = {
                items(
                    count = characterDataPoints.size,
                    key = { index -> index.hashCode() }, // مفتاح مميز
                ) { index ->

                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(end = 16.dp)) {


                        val list = listOf(DataPoint(title = "Name", description = character.name)) + characterDataPoints

                        val dataPoint = list[index]
                        DataPointComponent(sanitizeDataPoint(dataPoint))
                    }


                }
            }
        )
    }
}

private fun sanitizeDataPoint(dataPoint: DataPoint) : DataPoint{

    val newDesc = if (dataPoint.description.length >= 14){
        dataPoint.description.take(12) + "..."
    }else{
        dataPoint.description
    }

    return dataPoint.copy(description = newDesc)
}
