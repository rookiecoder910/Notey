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
        // Muted Pink/Rose
        "#FBCDCF",
        // Soft Yellow/Cream
        "#FFF9C4",
        // Light Sage Green
        "#DCF8C8",
        // Light Sky Blue
        "#C8E6FB",
        // Pale Lavender/Mauve
        "#E8D5F3",
        // Light Peach/Orange
        "#FFE0B2",
        // Off-White/Light Grey
        "#FAFAFA"
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
