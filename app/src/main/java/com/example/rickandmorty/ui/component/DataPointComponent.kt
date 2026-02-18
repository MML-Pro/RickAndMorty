package com.example.rickandmorty.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.rickandmorty.ui.theme.RickAction
import com.example.rickandmorty.ui.theme.RickTextPrimary

data class DataPoint(
    val title: String,
    val description: String
)
@Composable
fun DataPointComponent(dataPoint: DataPoint) {

    Column() {
        Text(
            text = dataPoint.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = RickAction
        )

        Text(
            text = dataPoint.description,
            fontSize = 24.sp,
            color = RickTextPrimary
        )
    }
}


@Composable
@Preview(showBackground = true)
fun DataPointComponentPreview() {
    val data = DataPoint(title = "Last known location", description = "Citadel of Ricks")
    DataPointComponent(data)
}