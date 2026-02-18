package com.example.rickandmorty.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rickandmorty.R
import com.example.rickandmorty.ui.theme.RickTextPrimary

@Composable
fun SimpleToolBar(title: String, onBackAction: (() -> Unit)? = null) {

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {

            if (onBackAction != null) {
                Box(
                    modifier = Modifier
                        .clickable { onBackAction() }
                        .padding(4.dp)
                        .background(shape = RoundedCornerShape(12.dp), color = Color.Transparent)
                        .clip(RoundedCornerShape(12.dp))

                ) {

                    Icon(
                        painter = painterResource(R.drawable.round_arrow_back_24),
                        contentDescription = "Back Arrow",
                        tint = RickTextPrimary,
                        modifier = Modifier.align(Alignment.Center).padding(4.dp).size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))


            Text(
                text = title, fontSize = 32.sp, style = TextStyle(
                    color = RickTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Box(
            modifier = Modifier
                .background(RickTextPrimary)
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}