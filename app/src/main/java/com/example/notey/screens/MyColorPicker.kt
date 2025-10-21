package com.example.notey.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@Composable
fun MyColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {

    val colorsList: List<Color> = listOf(
        "#f59597",
        "#FFD52E",
        "#C5E1A5",
        "#90CAF9",
        "#E1BEE7",
        "#FFCC80",
        "#E0E0E0"
    ).map { hex ->
        Color(hex.toColorInt())
    }

    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(colorsList) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .background(color=it)
                    .clip(CircleShape)
                    .border(
                        width = if (it == selectedColor) 4.dp else 0.dp,
                        color = if (it == selectedColor) Color.Black else Color.Transparent,
                        shape = CircleShape
                    )

                    .clickable {
                        onColorSelected(it)
                    }
            )
        }
    }
}
