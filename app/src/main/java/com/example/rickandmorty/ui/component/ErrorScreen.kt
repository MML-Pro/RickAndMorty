package com.example.rickandmorty.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorScreen(msg: String){

    Column(
        modifier = Modifier.fillMaxSize(),

        ) {
        Text(modifier = Modifier.fillMaxSize().padding(32.dp),
            text = "Error: $msg", fontSize = 32.sp,
            color = MaterialTheme.colorScheme.error
        )
    }
}