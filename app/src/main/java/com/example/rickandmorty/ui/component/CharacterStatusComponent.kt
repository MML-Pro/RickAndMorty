package com.example.rickandmorty.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.CharacterStatus
import com.example.rickandmorty.ui.theme.RickTextPrimary


@Composable
fun CharacterStatusComponent(characterStatus: CharacterStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(characterStatus.colorHex),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        val color = characterStatus.colorHex
        val displayName = characterStatus.displayName

        Text(
            text = "Status ${characterStatus.displayName}",
            fontSize = 20.sp,
            color = RickTextPrimary
        )

    }
}


@Composable
@Preview(showBackground = true)
fun CharacterStatusComponentPreviewAlive() {
    CharacterStatusComponent(characterStatus = CharacterStatus.Alive)
}